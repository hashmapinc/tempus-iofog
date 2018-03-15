package com.hashmapinc.tempus.edge.opcTagFilter.opc

import java.io.File
import java.util.Arrays
import scala.util.{Failure, Success, Try}

import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.sdk.client.api.config.{OpcUaClientConfig, OpcUaClientConfigBuilder}
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient
import com.typesafe.scalalogging.Logger

import com.hashmapinc.tempus.edge.opcTagFilter.Config
import com.hashmapinc.tempus.edge.track.proto.OpcConfig

object OpcConnection {
  private val logger = Logger(getClass())

  //create client
  var client: Option[OpcUaClient] = None
  
  /**
   * Creates a opcUaClient configuration
   *
   * @param opcEndpoint - String holding the opc endpoint to connect to
   * @param securityType - SecurityType protobuf object describing the security to use
   */
  def getClientConfig (
    opcEndpoint: String,
    securityType: OpcConfig.SecurityType
  ): OpcUaClientConfig = {
    logger.info("creating opc client configuration...")
    
    //=========================================================================
    // security configs
    //=========================================================================
    val securityTempDir = new File(System.getProperty("java.io.tmpdir"), "security")
    if (!securityTempDir.exists() && !securityTempDir.mkdirs()) {
      throw new Exception("unable to create security dir: " + securityTempDir)
    }
    logger.info("security temp dir: {}", securityTempDir.getAbsolutePath())

    // TODO: Implement other securityPolicy options
    val securityPolicy = 
      if (securityType == OpcConfig.SecurityType.NONE) SecurityPolicy.None
      else {
        logger.error("could not implement securityType: " + securityType)
        throw new Exception("unable to implement securityType: " + securityType)
      }

    //=========================================================================

    //=========================================================================
    // endpoint configs
    //=========================================================================
    val endpoints= Try({
      UaTcpStackClient.getEndpoints(opcEndpoint).get
    }).recoverWith({
      case e: Exception => {
        // try the explicit discovery endpoint as well
        val discoveryUrl = opcEndpoint + "/discovery"
        logger.info("Trying explicit discovery URL: {}", discoveryUrl)
        Success(UaTcpStackClient.getEndpoints(discoveryUrl).get)
      }
    })

    val endpoint = Arrays.stream(endpoints.get).
      filter(e => e.getSecurityPolicyUri().equals(securityPolicy.getSecurityPolicyUri())).
      findFirst().orElseThrow(()=> new Exception("no desired endpoints returned"))

    logger.info("Using endpoint: {} [{}]", endpoint.getEndpointUrl(), securityPolicy)
    //=========================================================================

    // return config
    OpcUaClientConfig.builder()
      .setApplicationName(LocalizedText.english("hashmapinc tempus edge opc client"))
      .setApplicationUri("urn:hashmapinc:tempus:edge:opc-client")
      .setEndpoint(endpoint)
      .setIdentityProvider(new AnonymousProvider())
      .setRequestTimeout(uint(5000))
      .build
  }

  /**
   * Creates a new opc client.
   *
   * @param opcEndpoint - String holding the opc endpoint to connect to
   * @param securityType - SecurityType protobuf object describing the security to use
   */
  def createOpcClient (
    opcEndpoint: String,
    securityType: OpcConfig.SecurityType
  ): OpcUaClient = {
    logger.info("creating new opc client..")
    new OpcUaClient(getClientConfig(opcEndpoint, securityType))
  }

  /**
   * updates the client attribute to the latest client instance with latest opc configuration
   */
  def updateClient: Unit = {
    val updatedClient = Try({
      val endpoint = Config.trackConfig.get.getOpcConfig.endpoint
      val securityType = Config.trackConfig.get.getOpcConfig.securityType
      Option(createOpcClient(endpoint, securityType))
    })

    if (updatedClient.isFailure)  logger.error("unable to update opc client. Will retry when new configuration arrives...")
    if (updatedClient.isSuccess)  logger.info("Successfully updated client.")

    client = updatedClient.getOrElse(None)
  }
}