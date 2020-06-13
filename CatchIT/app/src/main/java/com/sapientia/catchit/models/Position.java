package com.sapientia.catchit.models;

//TODO
//import com.example.sapientia.whacanodev2.ServerRelated.NodeClient;

import com.sapientia.catchit.serverrelated.NodeClient;

public class Position
{
    private int sequenceNumber;
    private String nodeName;
    private NodeClient assignedNode;

    public Position(int sequenceNumber){
        this.sequenceNumber = sequenceNumber;
    }

    public Position(){
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName (String nodeName) {
        this.nodeName = nodeName;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public NodeClient getAssignedNode()
    {
        return assignedNode;
    }

    public void setAssignedNode(NodeClient assignedNode)
    {
        this.assignedNode = assignedNode;
    }
}
