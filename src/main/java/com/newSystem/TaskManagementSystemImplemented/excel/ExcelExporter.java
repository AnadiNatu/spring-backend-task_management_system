package com.newSystem.TaskManagementSystemImplemented.excel;

import com.newSystem.TaskManagementSystemImplemented.dto.applicationDto.TaskExcelDTO;
import com.newSystem.TaskManagementSystemImplemented.entity.Task;
import com.newSystem.TaskManagementSystemImplemented.entity.TaskComment;
import com.newSystem.TaskManagementSystemImplemented.entity.Users;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

@Component
public class ExcelExporter {

    public ByteArrayInputStream exportTasks(List<Task> tasks) throws IOException{

        String[] columns = {"TaskIds" , "Title" , "Description" , "AssignedFrom Id" , "AssignedTo Id" , "PreviouslyAssigned To" , "Task CreatedOn" , "Assigned On" , "Completed At" , "Task Status"};

        try(Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()){

            Sheet sheet = workbook.createSheet();

            Row headerRow = sheet.createRow(0);
            for (int i = 0 ; i < columns.length ; i++){
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (Task task : tasks){
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(safeLong(task.getTaskIds()));
                row.createCell(1).setCellValue(safeString(task.getTaskTitle()));
                row.createCell(2).setCellValue(safeString(task.getTaskDescription()));
                row.createCell(3).setCellValue(safeLong(task.getAssignedFrom() != null ? task.getAssignedFrom().getId() : null));
                row.createCell(4).setCellValue(safeLong(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null));
                row.createCell(5).setCellValue(safeLong(task.getPreviouslyAssignedTo() != null ? task.getPreviouslyAssignedTo().getId() : null));
                row.createCell(6).setCellValue(safeString(task.getTaskCreatedOn() != null ? task.getTaskCreatedOn().toString() : null));
                row.createCell(7).setCellValue(safeString(task.getCompletedAt() != null ? task.getCompletedAt().toString() : null));
                row.createCell(8).setCellValue(safeString(task.getTaskStatus().toString()));

            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }

    }

    public ByteArrayInputStream exportUsers(List<Users> users)throws IOException{

        String[] columns = {"ID" , "Name" , "Username" , "Age" , "Department" , "Role"};

        try(Workbook workbook = new XSSFWorkbook() ; ByteArrayOutputStream out = new ByteArrayOutputStream()){

            Sheet sheet = workbook.createSheet("Users");

            Row headerRow = sheet.createRow(0);
            for (int i = 0 ; i < columns.length ; i++){
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (Users user : users) {

                Row row = sheet.createRow(rowIdx);
                row.createCell(0).setCellValue(safeLong(user.getId()));
                row.createCell(1).setCellValue(safeString(user.getName()));
                row.createCell(2).setCellValue(safeString(user.getUsername()));
                row.createCell(3).setCellValue(user.getAge());
                row.createCell(4).setCellValue(safeString(user.getDepartment()));
                row.createCell(5).setCellValue(safeString(user.getUserRoles().toString()));
            }

                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream exportComments(List<TaskComment> comments)throws IOException{

        String[] columns = {"ID" , "Task Title" , "Created By User Name" , "User Role" , "Content" , "Created At" , "Comment Role"};

        try(Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()){

            Sheet sheet = workbook.createSheet("Comments");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length ; i++){
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;

            for (TaskComment comment : comments){

                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(safeLong(comment.getId()));
                row.createCell(1).setCellValue(safeString(comment.getTasks() != null ? comment.getTasks().getTaskTitle(): null));
                row.createCell(2).setCellValue(safeString(comment.getCommentedByUser() != null ? comment.getCommentedByUser().getName() : null));
                row.createCell(3).setCellValue(safeString(comment.getCommentedByUser() != null ? comment.getCommentedByUser().getUserRoles().toString() : null));
                row.createCell(4).setCellValue(safeString(comment.getCommentContent() != null ? comment.getCommentContent() : null ));
                row.createCell(5).setCellValue( safeString(comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null));
                row.createCell(6).setCellValue(safeString(comment.getCommentByRole() != null ?comment.getCommentByRole().toString() : null));

            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }

    }


    @Value("${export.directory:exports}")
    private String exportDir;

    public File exportTaskToFile(List<Task> tasks)throws IOException{

        File directory = new File(exportDir);
        if (!directory.exists()) directory.mkdirs();

        File file = new File(directory , "tasks.xlsx");

        try(Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(file)){
            String[] columns = {"TaskIds" , "Title" , "Description" , "AssignedFrom Id" , "AssignedTo Id" , "PreviouslyAssigned To" , "Task CreatedOn" , "Assigned On" , "Completed At" , "Task Status"};

            Sheet sheet = workbook.createSheet();

            Row headerRow = sheet.createRow(0);
            for (int i = 0 ; i < columns.length ; i++){
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (Task task : tasks){
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(safeLong(task.getTaskIds() != null ? task.getTaskIds() : null ));
                row.createCell(1).setCellValue(safeString(task.getTaskTitle() != null ? task.getTaskTitle() : null ));
                row.createCell(2).setCellValue(safeString(task.getTaskDescription() != null ? task.getTaskDescription() : null));
                row.createCell(3).setCellValue(safeLong(task.getAssignedFrom() != null ? task.getAssignedFrom().getId() : null ));
                row.createCell(4).setCellValue(safeLong(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null));
                row.createCell(5).setCellValue(safeLong(task.getPreviouslyAssignedTo() != null ? task.getPreviouslyAssignedTo().getId() : null));
                row.createCell(6).setCellValue(safeString(task.getTaskCreatedOn() != null ? task.getTaskCreatedOn().toString() : null ));
                row.createCell(7).setCellValue(safeString(task.getAssignedOn() != null ? task.getAssignedOn().toString() : null));
                row.createCell(8).setCellValue(safeString(task.getCompletedAt() != null ? task.getCompletedAt().toString() : null));
                row.createCell(9).setCellValue(safeString(task.getTaskStatus() != null ? task.getTaskStatus().toString() : null));

            }
            workbook.write(fileOut);
        }
        return file;
    }

    public ByteArrayInputStream exportTasksToExcel(List<TaskExcelDTO> tasks) throws IOException {
        String[] headers = {
                "Task ID", "Title", "Department",
                "From User ID", "From User Name",
                "To User ID", "To User Name",
                "Previously Assigned ID", "Previously Assigned Name",
                "Status"
        };


        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Tasks");

            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Data rows
            int rowIdx = 1;
            for (TaskExcelDTO dto : tasks) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(safeLong(dto.getTaskId() != null ? dto.getTaskId() : null));
                row.createCell(1).setCellValue(safeString(dto.getTaskTitle() != null ? dto.getTaskTitle() : null));
                row.createCell(2).setCellValue(safeString(dto.getDepartment() != null ? dto.getDepartment() : null));
                row.createCell(3).setCellValue(safeLong(dto.getAssignedFromUserId() != null ? dto.getAssignedFromUserId() : null ));
                row.createCell(4).setCellValue(safeString(dto.getAssignedFromUserName() != null ? dto.getAssignedFromUserName() : null));
                row.createCell(5).setCellValue(safeLong(dto.getAssignedToUserId() != null ? dto.getAssignedToUserId() : null));
                row.createCell(6).setCellValue(safeString(dto.getAssignedToUserName() != null ? dto.getAssignedToUserName() : null));
                row.createCell(7).setCellValue(safeLong(dto.getPreviouslyAssignedToUserId() != null ? dto.getPreviouslyAssignedToUserId() : null));
                row.createCell(8).setCellValue(safeString(dto.getPreviouslyAssignedToUserName() != null ? dto.getPreviouslyAssignedToUserName() : null));
                row.createCell(9).setCellValue(safeString(dto.getTaskStatus() != null ? dto.getTaskStatus().toString() : null ));

            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public File exportTaskExcelDtoToSave(List<TaskExcelDTO> tasks) throws IOException {
        String[] headers = {
                "Task ID", "Title", "Department",
                "From User ID", "From User Name",
                "To User ID", "To User Name",
                "Previously Assigned ID", "Previously Assigned Name",
                "Status"
        };

        File directory = new File(exportDir);
        if (!directory.exists()) directory.mkdirs();

        File file = new File(directory, "tasks-dto.xlsx");

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(file)) {

            Sheet sheet = workbook.createSheet("Tasks");

            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Data rows
            int rowIdx = 1;
            for (TaskExcelDTO dto : tasks) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(safeLong(dto.getTaskId() != null ? dto.getTaskId() : null));
                row.createCell(1).setCellValue(safeString(dto.getTaskTitle() != null ? dto.getTaskTitle() : null));
                row.createCell(2).setCellValue(safeString(dto.getDepartment() != null ? dto.getDepartment() : null));
                row.createCell(3).setCellValue(safeLong(dto.getAssignedFromUserId() != null ? dto.getAssignedFromUserId() : null ));
                row.createCell(4).setCellValue(safeString(dto.getAssignedFromUserName() != null ? dto.getAssignedFromUserName() : null));
                row.createCell(5).setCellValue(safeLong(dto.getAssignedToUserId() != null ? dto.getAssignedToUserId() : null));
                row.createCell(6).setCellValue(safeString(dto.getAssignedToUserName() != null ? dto.getAssignedToUserName() : null));
                row.createCell(7).setCellValue(safeLong(dto.getPreviouslyAssignedToUserId() != null ? dto.getPreviouslyAssignedToUserId() : null));
                row.createCell(8).setCellValue(safeString(dto.getPreviouslyAssignedToUserName() != null ? dto.getPreviouslyAssignedToUserName() : null));
                row.createCell(9).setCellValue(safeString(dto.getTaskStatus() != null ? dto.getTaskStatus().toString() : null ));
            }

            workbook.write(fileOut);
        }

        return file;
    }

    private long safeLong(Long value){

        return value != null ? value : 0L;

    }

    private String safeString(String value){

        return value != null ? value : "N/A";

    }

}
