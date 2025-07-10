package com.newSystem.TaskManagementSystemImplemented.controller.export;

import com.newSystem.TaskManagementSystemImplemented.dto.applicationDto.TaskExcelDTO;
import com.newSystem.TaskManagementSystemImplemented.excel.ExcelExporter;
import com.newSystem.TaskManagementSystemImplemented.service.excelService.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/export/")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ExportController {

    private final ExcelExporter exporter;
    private final ExcelService excelService;


    @GetMapping("tasks")
    public ResponseEntity<Resource> exportTasks()throws IOException{

        ByteArrayInputStream stream = exporter.exportTasks(excelService.getAllTasks());
        return export(stream , "tasks.xlsx");

    }

    @GetMapping("users")
    public ResponseEntity<Resource> exportUsers()throws IOException{

        ByteArrayInputStream stream = exporter.exportUsers(excelService.getAllUsers());
        return export(stream , "users.xlsx");

    }

    @GetMapping("comments")
    public ResponseEntity<Resource> exportComments()throws IOException{

        ByteArrayInputStream stream = exporter.exportComments(excelService.getAllComments());
        return export(stream , "users.xlsx");

    }

    @GetMapping("/tasks/save")
    public ResponseEntity<String> saveTasksToFile(){
        try
        {
            File file = exporter.exportTaskToFile(excelService.getAllTasks());
            return ResponseEntity.ok("Tasks exported to: " + file.getAbsolutePath());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to export tasks : " + e.getMessage());
        }
    }

    @GetMapping("/tasks/getInfo")
    public ResponseEntity<Resource> saveTaskDtoExcelFile() throws IOException {
        List<TaskExcelDTO> excelData = (excelService.getTaskExcelDTO());

        ByteArrayInputStream in = exporter.exportTasksToExcel(excelData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition" , "attachment ; filename=tasks.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/tasks/getInfo/save")
    public ResponseEntity<String> saveTaskDtoExcelFileToDisk() {
        try {
            List<TaskExcelDTO> excelData = excelService.getTaskExcelDTO();
            File file = exporter.exportTaskExcelDtoToSave(excelData);
            return ResponseEntity.ok("Excel file saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to export tasks to file: " + e.getMessage());
        }
    }

    private ResponseEntity<Resource> export(ByteArrayInputStream stream , String fileName){

        InputStreamResource resource = new InputStreamResource(stream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION , "attachment; filename = " + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);

    }


}
