package com.ballistic.fserver.restapi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@Api(value="FServer Management System API", description="Operations on Server.")
public class FS3RestController {

    // Creating, Listing, and Deleting |S3| Buckets
    @ApiOperation(value = "Create New Bucket On S3", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/createBucket/s3/bucket={bucketName}", method = RequestMethod.POST)
    public void createBucket(@PathVariable(name = "bucketName") String bucketName) {}

    @ApiOperation(value = "Fetch All Bucket Name", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/listingBucket/s3/buckets", method = RequestMethod.GET)
    public void listingBucket() {}

    @ApiOperation(value = "Delete Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/deletingBucket/s3/bucket={bucketName}", method = RequestMethod.DELETE)
    public void deletingBucket(@PathVariable(name = "bucketName") String bucketName) {}

    @ApiOperation(value = "Save File To Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/saveFileTOBucket/s3/bucket={bucketName}", method = RequestMethod.POST)
    public void saveFileTOBucket(@PathVariable(name = "bucketName") String bucketName, @RequestParam(name = "file", required = true) MultipartFile file) {}

    @ApiOperation(value = "Save Files To Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = APIResponse.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/saveFilesToBucket/s3/bucket={bucketName}", method = RequestMethod.POST)
    public void saveFilesToBucket(@PathVariable(name = "bucketName") String bucketName, @RequestParam(value = "files", required = true) List<MultipartFile> files) {}

    @ApiOperation(value = "Delete File From Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = APIResponse.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/deleteFileFromBucket/s3/bucket={bucketName}/file={fileName}", method = RequestMethod.DELETE)
    public void deleteFileFromBucket(@PathVariable(name = "bucketName") String bucketName, @PathVariable(name = "fileName") String fileName) {}

    @ApiOperation(value = "Delete Files From Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = APIResponse.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/deleteFilesFromBucket/s3/bucket={bucketName}/files={fileNames}", method = RequestMethod.DELETE)
    public void deleteFilesFromBucket(@PathVariable(name = "bucketName") String bucketName, @PathVariable(value = "fileNames", required = true) List<String> fileNames) {}

    @ApiOperation(value = "Fetch File From Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = APIResponse.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/fetchFileFromBucket/s3/bucket={bucketName}/file={fileName}", method = RequestMethod.POST)
    public void fetchFileFromBucket(@PathVariable(name = "bucketName") String bucketName, @PathVariable(name = "fileName") String fileName) {}

    @ApiOperation(value = "Fetch Files From Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = APIResponse.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/fetchFilesFromBucket/s3/bucket={bucketName}//files={fileNames}", method = RequestMethod.POST)
    public void fetchFilesFromBucket(@PathVariable(name = "bucketName") String bucketName, @PathVariable(value = "fileNames", required = true) List<String> fileNames) {}

}
