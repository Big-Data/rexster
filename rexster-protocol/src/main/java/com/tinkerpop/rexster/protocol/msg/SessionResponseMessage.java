package com.tinkerpop.rexster.protocol.msg;

import org.msgpack.annotation.Message;

/**
 * Represents a response to a session request with a newly defined session and available ScriptEngine
 * languages or a closed session confirmation..
 */
@Message
public class SessionResponseMessage extends RexProMessage {
    public String[] Languages;
}
