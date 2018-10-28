package clientX86;

public class Message {
	String originalName;
	String name;
	int part;
	int qParts;
	String service;
	byte[] data;
	String paramsEncoding;
	String idForAck;
	
	
	
	public Message(String originalName, String name, int part, int qParts, String service, byte[] data, String paramsEncoding, String idForAck) {
		super();
		this.originalName = originalName;
		this.name = name;
		this.part = part;
		this.qParts = qParts;
		this.service = service;
		this.data = data;
		this.paramsEncoding = paramsEncoding;
		this.idForAck = idForAck;
		
	}
		public String getIdForAck() {
		return idForAck;
	}
	public void setIdForAck(String idForAck) {
		this.idForAck = idForAck;
	}
		public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPart() {
		return part;
	}
	public void setPart(int part) {
		this.part = part;
	}
	public int getqParts() {
		return qParts;
	}
	public void setqParts(int qParts) {
		this.qParts = qParts;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getParamsEncoding() {
		return paramsEncoding;
	}
	public void setParamsEncoding(String paramsEncoding) {
		this.paramsEncoding = paramsEncoding;
	}
	


}
