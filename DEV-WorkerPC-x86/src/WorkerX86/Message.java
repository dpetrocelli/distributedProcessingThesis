package WorkerX86;


public class Message {
	
	String originalName;
	String name;
	int part;
	int qParts;
	String service;
	byte[] data;
	String encodingProfiles;
	String paramsEncoding;
	String idForAck;
	String workerName;
	String workerArchitecture;
	long  initTime;
	long endTime;
	int totalTime;
	
	public Message () {
	
	}
	public Message(String originalName, String name, int part, int qParts, String service, byte[] data, String encodingProfiles, String paramsEncoding, String idForAck) {
		super();
		this.originalName = originalName;
		this.name = name;
		this.part = part;
		this.qParts = qParts;
		this.service = service;
		this.data = data;
		this.paramsEncoding = paramsEncoding;
		this.idForAck = idForAck;
		this.encodingProfiles = encodingProfiles;
		
	}
	
	public Message(String originalName, String name, int part, int qParts, String service, byte[] data, String paramsEncoding, String idForAck, String workerName, String workerArchitecture, long initTime, long endTime, int executionTime) {
		super();
		this.originalName = originalName;
		this.name = name;
		this.part = part;
		this.qParts = qParts;
		this.service = service;
		this.data = data;
		this.paramsEncoding = paramsEncoding;
		this.idForAck = idForAck;
		this.workerName = workerName;
		this.workerArchitecture = workerArchitecture;
		this.initTime = initTime;
		this.endTime = endTime;
		this.totalTime = totalTime;
	}
	
	public String getEncodingProfiles() {
		return encodingProfiles;
	}

	public void setEncodingProfiles(String encodingProfiles) {
		this.encodingProfiles = encodingProfiles;
	}
	
	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public String getWorkerArchitecture() {
		return workerArchitecture;
	}

	public void setWorkerArchitecture(String workerArchitecture) {
		this.workerArchitecture = workerArchitecture;
	}

	public long getInitTime() {
		return initTime;
	}

	public void setInitTime(long initTime) {
		this.initTime = initTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
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
