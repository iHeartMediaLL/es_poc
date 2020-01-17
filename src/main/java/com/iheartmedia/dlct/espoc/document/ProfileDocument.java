package com.iheartmedia.dlct.espoc.document;

import com.iheartmedia.dlct.espoc.model.Technologies;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileDocument {
    private String id;
    private String firstName;
    private String lastName;
    private List<Technologies> technologies;
    private List<String> emails;
}
