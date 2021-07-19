package com.bezkoder.spring.files.upload.db.message;

public class ResponseFile {
  private String id;	
  private String name;
  private String url;
  private String tepmUrl;
  private String type;
  private long size;
  private String template;
 
  public ResponseFile(String id,String name, String url, String tepmUrl, String type, long size,String template) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
		this.tepmUrl = tepmUrl;
		this.type = type;
		this.size = size;
		this.template=template;
  	}

	public String getTemplate() {
		return template;
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTepmUrl() {
		return tepmUrl;
	}
	
	public void setTepmUrl(String tepmUrl) {
		this.tepmUrl = tepmUrl;
	}
	
	public String getName() {
	    return name;
	  }
	
	  public void setName(String name) {
	    this.name = name;
	  }
	
	  public String getUrl() {
	    return url;
	  }
	
	  public void setUrl(String url) {
	    this.url = url;
	  }
	
	  public String getType() {
	    return type;
	  }
	
	  public void setType(String type) {
	    this.type = type;
	  }
	
	  public long getSize() {
	    return size;
	  }
	
	  public void setSize(long size) {
	    this.size = size;
	  }
	  
	@Override
	public String toString() {
		return "ResponseFile [id=" + id + ", name=" + name + ", url=" + url + ", tepmUrl=" + tepmUrl + ", type=" + type
				+ ", size=" + size + ", template=" + template + "]";
	}

}
