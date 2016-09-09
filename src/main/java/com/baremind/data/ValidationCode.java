package com.baremind.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "validation_codes")
public class ValidationCode {
	@Id
    @Column(name = "id")
	private Long id;
	
	@Column(name = "phone_number")
	private String phoneNumber;//手机号
	
	@Column(name = "valid_code")
	private String validCode;//验证码
	
	@Column(name = "timestamp")
	private Date timestamp;//当前时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getValidCode() {
		return validCode;
	}

	public void setValidCode(String validCode) {
		this.validCode = validCode;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
