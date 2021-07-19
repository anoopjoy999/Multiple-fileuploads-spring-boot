package com.bezkoder.spring.files.upload.db.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bezkoder.spring.files.upload.db.service.FileStorageService;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.bezkoder.spring.files.upload.db.message.ResponseFile;
import com.bezkoder.spring.files.upload.db.message.ResponseMessage;
import com.bezkoder.spring.files.upload.db.model.FileDB;

@Controller
@CrossOrigin("http://localhost:4200")
public class FileController {

  @Autowired
  private FileStorageService storageService;

  @PostMapping("/upload")
  public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
    String message = "";
    try {
    	
    	List<Student> students = getStudents(file);
    	students.forEach(System.out::println);
      storageService.store(file);

      message = "Uploaded the file successfully: " + file.getOriginalFilename();
      return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    } catch (Exception e) {
      message = "Could not upload the file: " + file.getOriginalFilename() + "!";
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
    }
  }

  public List<Student> getStudents(MultipartFile file) throws Exception{
	  
	  List<Student> students = null;
	  Map<String,String> mapping = new HashMap<String,String>();
	  mapping.put("Name","name");
	  mapping.put("Class", "clas");
	  mapping.put("Marks", "mark");
	  
	  HeaderColumnNameTranslateMappingStrategy<Student> strategy = new HeaderColumnNameTranslateMappingStrategy<>();
	  strategy.setType(Student.class);
	  strategy.setColumnMapping(mapping);
	  Reader reader = null;
	  CSVReader csvReader = null;
	  
	  InputStream is = file.getInputStream();
	  reader = new BufferedReader(new InputStreamReader(is));
	  csvReader = new CSVReader(reader);
	  
	  CsvToBean<Student> csvToBean = new CsvToBean<>();
	  csvToBean.setCsvReader(csvReader);
	  csvToBean.setMappingStrategy(strategy);
	  students = csvToBean.parse();
	  
	  return students;
	  
  }
  
  @GetMapping("/files")
  public ResponseEntity<List<ResponseFile>> getListFiles() {
    List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
      String fileDownloadUri = ServletUriComponentsBuilder
          .fromCurrentContextPath()
          .path("/file/")
          .path(dbFile.getId())
          .toUriString();
      
      String templateDownloadUri = ServletUriComponentsBuilder
              .fromCurrentContextPath()
              .path("/template/")
              .path(dbFile.getTemplate())
              .toUriString();

      return new ResponseFile(
    		  dbFile.getId(),
          dbFile.getName(),
          fileDownloadUri,
          templateDownloadUri,
          dbFile.getType(),
          dbFile.getData().length,
          dbFile.getTemplate());
    }).collect(Collectors.toList());

    return ResponseEntity.status(HttpStatus.OK).body(files);
  }

  @PostMapping("/file")
  public ResponseEntity<byte[]> getFile(@RequestBody ResponseFile request) throws Exception{
    FileDB fileDB = storageService.getFile(request.getId());
    try {
	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
	            //.contentLength(fileDB.getData().length)
		        //.contentType(MediaType.parseMediaType("text/csv"))
	            .body(fileDB.getData());
    	}catch(Exception e) {
    		throw e;
    	}finally {
    }
  }
  
  @PostMapping("/template")
  public ResponseEntity<byte[]> getTemplateFile(@RequestBody ResponseFile request) throws Exception{
	  File file = null;
    try {
	    file = getTemplate("rule_template");
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
	        .contentLength(file.length())
	        .contentType(MediaType.parseMediaType("text/csv"))
	        .body(fileContent);
    	}catch(Exception e) {
    		throw e;
    	}finally {
    		file.delete();
    	}
  }
  
  
  @GetMapping("/file/{id}")
  public ResponseEntity<byte[]> getFile(@PathVariable String id) throws Exception{
    FileDB fileDB = storageService.getFile(id);
    try {
	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
	            .body(fileDB.getData());
    	}catch(Exception e) {
    		throw e;
    	}finally {
    }
  }
  
  @GetMapping("/template/{id}")
  public ResponseEntity<byte[]> getTemplateFile(@PathVariable String id) throws Exception{
	  File file = null;
    try {
	    file = getTemplate("rule_template");
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
	        .body(fileContent);
    	}catch(Exception e) {
    		throw e;
    	}finally {
    		file.delete();
    	}
  }
  
  public File getTemplate(String type) throws Exception{
	  String fileName = "template.csv";
	  String folderName = "uploads";
	  File newResourceFolder = createResourceSubfolder(folderName);
	  File file = new File(newResourceFolder,fileName);
	  FileWriter outputfile = new FileWriter(file);
	  CSVWriter writer = new CSVWriter(outputfile);
	  List<String[]> data = new ArrayList<>();
	  data.add(new String[] {"Name","Class","Marks"});
	  writer.writeAll(data);
	  writer.close();
	  return file;
  }
  
  public File createResourceSubfolder(String folderName) throws Exception{
	  
	  URL url = FileController.class.getResource("/");
	  File fullPathToSubfolder = new File(url.toURI()).getAbsoluteFile();
	  String projectFolder = fullPathToSubfolder.getAbsolutePath().split("target")[0];
	  File testResultsFolder = new File(projectFolder + "src/main/resources/" + folderName);
	  if(!testResultsFolder.exists()) {
		  testResultsFolder.mkdir();
	  }
	  return testResultsFolder;
  }
}
