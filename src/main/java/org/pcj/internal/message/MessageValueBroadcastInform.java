/*
 * This file is the internal part of the PCJ Library
 */
package org.pcj.internal.message;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import org.pcj.internal.InternalCommonGroup;
import org.pcj.internal.InternalPCJ;
import org.pcj.internal.NodeData;
import org.pcj.internal.futures.BroadcastState;
import org.pcj.internal.network.MessageDataInputStream;
import org.pcj.internal.network.MessageDataOutputStream;

/**
 * ....
 *
 * @author Marek Nowicki (faramir@mat.umk.pl)
 */
final public class MessageValueBroadcastInform extends Message {

    private int groupId;
    private int requestNum;
    private int requesterThreadId;
    private int physicalId;

    public MessageValueBroadcastInform() {
        super(MessageType.VALUE_BROADCAST_INFORM);
    }

    public MessageValueBroadcastInform(int requestNum, int groupId, int requesterThreadId, int physicalId) {
        this();

        this.groupId = groupId;
        this.requestNum = requestNum;
        this.requesterThreadId = requesterThreadId;
        this.physicalId = physicalId;
    }

    @Override
    public void write(MessageDataOutputStream out) throws IOException {
        out.writeInt(groupId);
        out.writeInt(requestNum);
        out.writeInt(requesterThreadId);
        out.writeInt(physicalId);
    }

    @Override
    public void execute(SocketChannel sender, MessageDataInputStream in) throws IOException {
                requestNum = in.readInt();
        groupId = in.readInt();
        requesterThreadId = in.readInt();
        physicalId = in.readInt();

        NodeData nodeData = InternalPCJ.getNodeData();
        InternalCommonGroup group = nodeData.getGroupById(groupId);
        
        BroadcastState broadcastState = group.getBroadcastState(requestNum, requesterThreadId);
        broadcastState.processPhysical(physicalId);

    }
}