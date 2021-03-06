# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: MessageTypes.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf.internal import enum_type_wrapper
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
from google.protobuf import descriptor_pb2
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='MessageTypes.proto',
  package='com.hashmapinc.tempus.edge.proto',
  syntax='proto3',
  serialized_pb=_b('\n\x12MessageTypes.proto\x12 com.hashmapinc.tempus.edge.proto*\xbb\x01\n\x12\x43onfigMessageTypes\x12\x10\n\x0cUPDATE_ALERT\x10\x00\x12\x1b\n\x17TRACK_CONFIG_SUBMISSION\x10\x01\x12\x1d\n\x19TRACK_METADATA_SUBMISSION\x10\x02\x12\x1a\n\x16MQTT_CONFIG_SUBMISSION\x10\x03\x12\x19\n\x15OPC_CONFIG_SUBMISSION\x10\x04\x12 \n\x1cOPC_SUBSCRIPTIONS_SUBMISSION\x10\x05*/\n\x10\x44\x61taMessageTypes\x12\x08\n\x04JSON\x10\x00\x12\x08\n\x04MQTT\x10\x01\x12\x07\n\x03OPC\x10\x02\x42\"Z com/hashmapinc/tempus/edge/protob\x06proto3')
)

_CONFIGMESSAGETYPES = _descriptor.EnumDescriptor(
  name='ConfigMessageTypes',
  full_name='com.hashmapinc.tempus.edge.proto.ConfigMessageTypes',
  filename=None,
  file=DESCRIPTOR,
  values=[
    _descriptor.EnumValueDescriptor(
      name='UPDATE_ALERT', index=0, number=0,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='TRACK_CONFIG_SUBMISSION', index=1, number=1,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='TRACK_METADATA_SUBMISSION', index=2, number=2,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='MQTT_CONFIG_SUBMISSION', index=3, number=3,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='OPC_CONFIG_SUBMISSION', index=4, number=4,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='OPC_SUBSCRIPTIONS_SUBMISSION', index=5, number=5,
      options=None,
      type=None),
  ],
  containing_type=None,
  options=None,
  serialized_start=57,
  serialized_end=244,
)
_sym_db.RegisterEnumDescriptor(_CONFIGMESSAGETYPES)

ConfigMessageTypes = enum_type_wrapper.EnumTypeWrapper(_CONFIGMESSAGETYPES)
_DATAMESSAGETYPES = _descriptor.EnumDescriptor(
  name='DataMessageTypes',
  full_name='com.hashmapinc.tempus.edge.proto.DataMessageTypes',
  filename=None,
  file=DESCRIPTOR,
  values=[
    _descriptor.EnumValueDescriptor(
      name='JSON', index=0, number=0,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='MQTT', index=1, number=1,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='OPC', index=2, number=2,
      options=None,
      type=None),
  ],
  containing_type=None,
  options=None,
  serialized_start=246,
  serialized_end=293,
)
_sym_db.RegisterEnumDescriptor(_DATAMESSAGETYPES)

DataMessageTypes = enum_type_wrapper.EnumTypeWrapper(_DATAMESSAGETYPES)
UPDATE_ALERT = 0
TRACK_CONFIG_SUBMISSION = 1
TRACK_METADATA_SUBMISSION = 2
MQTT_CONFIG_SUBMISSION = 3
OPC_CONFIG_SUBMISSION = 4
OPC_SUBSCRIPTIONS_SUBMISSION = 5
JSON = 0
MQTT = 1
OPC = 2


DESCRIPTOR.enum_types_by_name['ConfigMessageTypes'] = _CONFIGMESSAGETYPES
DESCRIPTOR.enum_types_by_name['DataMessageTypes'] = _DATAMESSAGETYPES
_sym_db.RegisterFileDescriptor(DESCRIPTOR)


DESCRIPTOR.has_options = True
DESCRIPTOR._options = _descriptor._ParseOptions(descriptor_pb2.FileOptions(), _b('Z com/hashmapinc/tempus/edge/proto'))
# @@protoc_insertion_point(module_scope)
