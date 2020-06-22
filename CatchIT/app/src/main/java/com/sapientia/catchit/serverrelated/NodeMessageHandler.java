package com.sapientia.catchit.serverrelated;

public interface NodeMessageHandler
{
    void handleMessage(NodeClient node, String message);
}
