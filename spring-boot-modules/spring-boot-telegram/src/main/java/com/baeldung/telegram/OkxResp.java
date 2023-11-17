package com.baeldung.telegram;

import java.util.List;

public class OkxResp {
	
	private String code;
    private String msg;
    private List<OkxRateData> data;
    
    
    
    public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}



	public String getMsg() {
		return msg;
	}



	public void setMsg(String msg) {
		this.msg = msg;
	}



	public List<OkxRateData> getData() {
		return data;
	}



	public void setData(List<OkxRateData> data) {
		this.data = data;
	}



	public class OkxRateData {
    	private String usdCny;

		public String getUsdCny() {
			return usdCny;
		}

		public void setUsdCny(String usdCny) {
			this.usdCny = usdCny;
		}
    }

}
