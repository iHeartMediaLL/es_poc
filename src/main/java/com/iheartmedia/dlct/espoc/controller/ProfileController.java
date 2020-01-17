package com.iheartmedia.dlct.espoc.controller;

import com.iheartmedia.dlct.espoc.document.ProfileDocument;
import com.iheartmedia.dlct.espoc.service.ElasticHelper;
import com.iheartmedia.dlct.espoc.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/es")
public class ProfileController {

    private ProfileService profileService;

    private ElasticHelper elasticHelper;

    @Autowired
    public ProfileController(ProfileService profileService, ElasticHelper elasticHelper){
        this.profileService = profileService;
        this.elasticHelper = elasticHelper;
    }

    @PostMapping(value = "/profiles")
    public ResponseEntity createProfile(@RequestBody ProfileDocument profileDocument) throws Exception {
        return new ResponseEntity(profileService.createProfileDocument(profileDocument), HttpStatus.CREATED);
    }

    @PutMapping(value = "/profiles")
    public ResponseEntity updateProfile(@RequestBody ProfileDocument profileDocument) throws Exception {
        return new ResponseEntity(profileService.updateProfileDocument(profileDocument), HttpStatus.CREATED);
    }

    @GetMapping(value = "/profiles/{id}")
    public ProfileDocument findById(@PathVariable String id) throws Exception {
        return profileService.findById(id);
    }

    @GetMapping(value = "/profiles")
    public List<ProfileDocument> findAll() throws Exception {
        return profileService.findAll();
    }

    @GetMapping(value = "/search")
    public List<ProfileDocument> search(@RequestParam(value = "technology") String technology) throws Exception {
        return profileService.searchByTechnology(technology);
    }

    @DeleteMapping(value = "/profiles/{id}")
    public String deleteProfile(@PathVariable String id) throws Exception {
        return profileService.deleteProfileDocument(id);
    }

    @PostMapping(value = "/create-index")
    public Boolean createIndex(JSONObject index) throws Exception {
        return elasticHelper.createIndexWithMapping(index);
    }

    @DeleteMapping(value = "/delete-index")
    public Boolean deleteIndex() throws Exception {
        return elasticHelper.deleteIndex();
    }

}
