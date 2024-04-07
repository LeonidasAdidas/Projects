public class DataPacket {
    public int senderID = -1;
    public DataType dataType;
    public Data content;
    public DataPacket(DataType dataType, Data content) {
        this.dataType = dataType;
        this.content = content;
    }
    public DataPacket(int senderID, DataType dataType, Data content) {
        this.dataType = dataType;
        this.content = content;
        this.senderID = senderID;
    }
}
