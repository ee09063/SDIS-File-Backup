
public class Chunk {
	public static final int CHUNK_MAX_SIZE = 64000;
	
	public final int chunkNo;
	public final Integer replicationDeg;
	public final FileID fileID;
	public final byte[] data;
	
	public Chunk(int cn, int rd, FileID fileID, byte[] data){
		if(cn < 0 || cn > 999999) throw new IllegalArgumentException("ERROR: Chunk number outside allowed values");
		this.chunkNo = cn;
		if(rd < 1 || rd > 9) throw new IllegalArgumentException("ERROR: Replication Degree of Chunk outside allowed Values");
		this.replicationDeg = rd;
		this.fileID = fileID;
		if(data.length > CHUNK_MAX_SIZE) throw new IllegalArgumentException("ERROR: Data larger than CHUNK_MAX_SIZE");
		this.data = data;
	}
}
