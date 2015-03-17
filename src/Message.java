import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class Message {
	public enum Type{
		PUTCHUNK, GETCHUNK, CHUNK, STORED
	}
	
	public final Type type;
	
	public Message(Type type){
		this.type=type;
	}
	
	private FileID fileID = null;
	
	protected void setFileID(byte[] fileID){
		this.fileID = new FileID(fileID); 
	}
	
	protected void setFileID(FileID fileID){
		this.fileID = fileID;
	}
	
	public String getHexFileID(){
		return this.fileID.toString();
	}
	
	private byte[] version = null;
	
	protected void setVersion(int i, int j){
		if(version == null){
			version = new byte[2];
		}
		version[0] = (byte)i;
		version[1] = (byte)j;
	}
	
	public int[] getVersion() {
		return new int[]{(int) version[0], (int) version[1]};
	}
	
	public Integer chunkNo = null;
	
	private Byte replicationDeg = null;
	
	private byte[] body = null;
	
	protected void setChunkNo(int c){
		this.chunkNo = c;
	}
	
	public Integer getChunkNo(){
		return this.chunkNo;
	}
	
	public Integer getReplicationDeg(){
		return this.replicationDeg.intValue();
	}
	
	protected void setReplicationDegree(int i){
		this.replicationDeg = (byte)i;
	}
	
	protected void setBody(byte[] b){
		this.body = b;
	}
	
	public byte[] getBody(){
		return this.body;
	}
	
	public byte[] toByteArray(){
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.type.toString());
		
		if(this.version != null){
			sb.append(' ' + Byte.toString(version[0]) + '.' + Byte.toString(version[1]));
		}
		
		if(this.fileID != null){
			sb.append(' ');
			String fileIdStr = fileID.toString();
			sb.append(fileIdStr);
		}
		
		if(this.chunkNo != null){
			sb.append(' ' + this.chunkNo.toString());
		}
		
		if(this.replicationDeg != null){
			sb.append(' ' + this.replicationDeg.toString());
		}
		
		sb.append("\r\n\r\n");
		
		byte[] result;
		
		result = sb.toString().getBytes(StandardCharsets.US_ASCII);
		
		if(this.body != null){
			int prevLength = result.length;
			result = Arrays.copyOf(result, prevLength + this.body.length);
			System.arraycopy(body, 0, result, prevLength, body.length);
		}
		
		return result;
	}
	
	public static Message makePutChunk(Chunk chunk){
		Message result = new Message(Type.PUTCHUNK);
		
		result.setVersion(1, 0);
		result.setChunkNo(chunk.chunkNo);
		result.setReplicationDegree(chunk.replicationDeg);
		result.setFileID(chunk.fileID);
		result.setBody(chunk.data);
		
		return result;
	}
	
	public static Message makeStored(FileID fileID, int chunkNo){
		Message result = new Message(Type.STORED);
		
		result.setVersion(1, 0);
		result.setFileID(fileID);
		result.setChunkNo(chunkNo);
		
		return result;
	}
	
	@SuppressWarnings("deprecation")
	public static Message fromByteArray(byte[] bArray) throws IOException{
		ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(bArray);
		DataInputStream dis = new DataInputStream(byteArrayStream);
		
		String str = null;
		try {
			str = dis.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String[] msgParams = str.split(" ");
		
		int paramNum = 0;
		Type t = null;
		try{
			t = Type.valueOf(msgParams[paramNum]);
		} catch(Exception e){
			throw new IOException("Unrecognized Message Type.");
		}
		
		Message msg = new Message(t);
		
		paramNum++;
		String fileID = msgParams[paramNum];
		String[] chars = fileID.split("(?<=\\G)");
		byte[] tempFileID = new byte[32];
		
		for(int i = chars.length - 1; i >= 0; --i){
			tempFileID[(tempFileID.length-1) - i] = (byte) Short.parseShort(chars[i],16);
		}
		
		msg.setFileID(tempFileID);
		
		if(msg.type == Type.PUTCHUNK){
			paramNum++;
			String replicationDegStr = msgParams[paramNum];
			msg.replicationDeg = Byte.parseByte(replicationDegStr);
		}
		
		if(msg.type == Type.PUTCHUNK){
			dis.skip(2);
			msg.body = new byte[dis.available()];
			dis.readFully(msg.body);
		}
		
		return msg;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
