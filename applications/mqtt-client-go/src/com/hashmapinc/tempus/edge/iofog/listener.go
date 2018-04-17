package iofog

import (
	sdk "github.com/iofog/container-sdk-go"
)

// Listener is a function type for handling iofog data websocket events
type Listener func(*sdk.IoMessage) error
