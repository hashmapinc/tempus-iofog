package com.hashmapinc.tempus.edge.opc

import scala.util.Try

import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.sdk.client.api.config.{OpcUaClientConfig, OpcUaClientConfigBuilder}
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient
import com.typesafe.scalalogging.Logger

import com.hashmapinc.tempus.edge.proto.OpcConfig

object OpcConnection {
  private val log = Logger(getClass())

  // create client
  var client: Option[OpcUaClient] = None
  
  /**
   *  Creates an opcUaClient configuration
   *
   *  This function is not fault tolerant. The caller should handle errors.
   *
   *  @param opcConf           - OpcConfig proto object with opc configuration to use
   *
   *  @return opcClientConfig  - OpcUaClientConfig created from opcConf
   */
  def getClientConfig (
    opcConf: OpcConfig
  ): OpcUaClientConfig = {
    log.info("creating opc client configuration...")

    //=========================================================================
    // setup endpoint
    //=========================================================================
    val securityPolicy  = OpcSecurity.getSecurityPolicy(opcConf)
    val securityMode    = OpcSecurity.getSecurityMode(opcConf)
    val opcEndpoint     = opcConf.endpoint   

    // get all endpoints available at opcEndpoint that match the securityPolicy
    val endpoints = Try({
      UaTcpStackClient.getEndpoints(opcEndpoint).get
    }).recover({
      case e: Exception => {
        // try the explicit discovery endpoint as well
        val discoveryUrl = opcEndpoint + "/discovery"
        log.info("Trying explicit discovery URL: {}", discoveryUrl)
        UaTcpStackClient.getEndpoints(discoveryUrl).get
      }
    }).get.filter( endpoint =>
      endpoint.getSecurityPolicyUri == securityPolicy.getSecurityPolicyUri 
      && endpoint.getSecurityMode   == securityMode
    )

    // get endpoint from filtered endpoints
    val endpoint = Try(endpoints(0))
    if (endpoint.isSuccess) 
      log.info("Using endpoint: {} [{}]", endpoint.get.getEndpointUrl(), securityPolicy)
    else {
      log.error("No endpoints with proper security policies were found for securityPolicy = {} at opcEndpoint = ", securityPolicy, opcEndpoint)
      throw new Exception("Could not connect")
    }
    //=========================================================================

    // return config
    OpcUaClientConfig.builder()
      .setApplicationName(LocalizedText.english("Hashmapinc Tempus Edge OPC Client"))
      .setApplicationUri("urn:hashmapinc:tempus:edge:opc-client")
      .setCertificate(OpcSecurity.clientCertificate)
      .setKeyPair(OpcSecurity.clientKeyPair)
      .setEndpoint(endpoint.get)
      .setIdentityProvider(OpcSecurity.getIdentityProvider(opcConf))
      .setRequestTimeout(uint(5000))
      .build
  }

  /**
   * Creates a new opc client.
   *
   * @param opcConf    - OpcConfig proto object with opc configuration to use
   *
   * @return opcClient - new opc client
   */
  def createOpcClient (
    opcConf: OpcConfig
  ): OpcUaClient = {
    log.info("creating new opc client..")
    new OpcUaClient(getClientConfig(opcConf))
  }

  /**
   * recursion utility for retrying client updating
   *
   * @param updatedClient           - Try[OpcUaClient] from the previous caller
   * @param opcConfig               - opcConfig to use for connecting
   * @param remainingAttempts       - Int value of number of retries remaining
   *
   * @return successfulUpdateClient - Try[OpcUaClient] result of updateClient attempt
   */
  @scala.annotation.tailrec
  def recursiveUpdateClient(
    updatedClient:      Try[OpcUaClient],
    opcConfig:          OpcConfig,
    remainingAttempts:  Int
  ): Try[OpcUaClient] = {
    if (updatedClient.isSuccess || remainingAttempts < 1) updatedClient else {
      val reconnectionDelay = 10000L // TODO: extract this value from the opcConfig
      log.error("Could not update OPC client. Received error: " + updatedClient.failed.get)
      log.error("Will retry connection {} more time(s) in {} milliseconds...", remainingAttempts, reconnectionDelay)
      Thread.sleep(reconnectionDelay) 
      recursiveUpdateClient(Try(createOpcClient(opcConfig)), opcConfig, remainingAttempts - 1)
    }
  }

  /**
   *  updates the client attribute to the latest client instance with latest 
   *  opc configuration.
   *
   *  @param opcConfig - tempus edge opocConfig proto object
   */
  def updateClient(
    opcConfig: Try[OpcConfig]
  ): Unit = {
    this.synchronized {
      // destroy the existing client if it exists
      if (client.isDefined) {
        client.get.getSubscriptionManager.clearSubscriptions
        client.get.disconnect
      }

      // if opcConfig exists and has an endpoint, let's connect!
      if (opcConfig.isFailure || opcConfig.get.endpoint.isEmpty)
        log.error("Could not update OPC client: no suitable OPC Configuration found. Will retry when new configs arrive.")
      else {
        client = recursiveUpdateClient(Try(createOpcClient(opcConfig.get)), opcConfig.get, 5).toOption // TODO: paramterize 5 to be a variable from the opcConfig.
        if (client.isDefined)
          log.info("OPC client successfully updated!")
        else 
          log.warn("OPC client update was unsuccessful. Will retry when new OPC configs arrive.")
      }
    }
  }
}