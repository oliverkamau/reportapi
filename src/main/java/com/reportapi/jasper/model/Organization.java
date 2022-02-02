package com.reportapi.jasper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    private String name;

    private String address;

    private String phone;

    private String mobile;

    private String website;

    private String box;

    private String email;

    private String location;

    private byte[] orgLogo;

}
