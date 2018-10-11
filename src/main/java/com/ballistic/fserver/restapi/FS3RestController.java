package com.ballistic.fserver.restapi;

import com.ballistic.fserver.manager.FileStoreManager;
import com.ballistic.fserver.manager.ManagerResponse;
import com.ballistic.fserver.service.FileStoreService;
import com.ballistic.fserver.service.IAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@Api(value="FServer Management System API", description="Operations on Server.")
public class FS3RestController {


    private static final Logger logger = LogManager.getLogger(FS3RestController.class);

    private APIResponse<?> apiResponse = null;
    private List<APIResponse<?>> apiResponses = null;
    private ManagerResponse<?> managerResponse = null;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileStoreManager fileStoreManager;
    @Autowired
    private FileStoreService ifileStoreService;
    @Autowired
    private IAccountService iAccountService;

    // Creating, Listing, and Deleting |S3| Buckets
    // important
    @ApiOperation(value = "Create New Bucket On S3", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/createBucket/s3/bucket={bucketName}", method = RequestMethod.POST)
    public void createBucket(@PathVariable(name = "bucketName", value = "ballfserver") String bucketName) {

    }

    // important
    @ApiOperation(value = "Fetch All Bucket Name", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/listingBucket/s3/buckets", method = RequestMethod.GET)
    public void listingBucket() {

    }

    // important
    @ApiOperation(value = "Delete Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/deletingBucket/s3/bucket={bucketName}", method = RequestMethod.DELETE)
    public void deletingBucket(@PathVariable(name = "bucketName", value = "ballfserver") String bucketName) {

    }

    // important
    @ApiOperation(value = "Save File To Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/saveFileTOBucket/s3/bucket={bucketName}", method = RequestMethod.POST)
    public void saveFileTOBucket(@PathVariable(name = "bucketName", value = "ballfserver") String bucketName,
           @RequestParam(name = "file", required = true) MultipartFile file) {

    }

    // important
    @ApiOperation(value = "Save Files To Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = APIResponse.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/saveFilesToBucket/s3/bucket={bucketName}", method = RequestMethod.POST)
    public void saveFilesToBucket(@PathVariable(name = "bucketName", value = "ballfserver") String bucketName,
           @RequestParam(value = "files", required = true) List<MultipartFile> files) {

    }

    // important
    @ApiOperation(value = "Delete File From Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = APIResponse.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/deleteFileFromBucket/s3/bucket={bucketName}/file={fileName}", method = RequestMethod.DELETE)
    public void deleteFileFromBucket(@PathVariable(name = "bucketName") String bucketName, @PathVariable(name = "fileName") String fileName) {

    }

    // important
    @ApiOperation(value = "Delete Files From Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = APIResponse.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/deleteFilesFromBucket/s3/bucket={bucketName}/files={fileNames}", method = RequestMethod.DELETE)
    public void deleteFilesFromBucket(@PathVariable(name = "bucketName", value = "ballfserver") String bucketName,
           @PathVariable(value = "fileNames", required = true) List<String> fileNames) {

    }

    // important
    @ApiOperation(value = "Fetch File From Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = APIResponse.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/fetchFileFromBucket/s3/bucket={bucketName}/file={fileName}", method = RequestMethod.POST)
    public void fetchFileFromBucket(@PathVariable(name = "bucketName", value = "ballfserver") String bucketName,
           @PathVariable(name = "fileName") String fileName) {

    }

    // important
    @ApiOperation(value = "Fetch Files From Bucket", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = APIResponse.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/fetchFilesFromBucket/s3/bucket={bucketName}/files={fileNames}", method = RequestMethod.POST)
    public void fetchFilesFromBucket(@PathVariable(name = "bucketName", value = "ballfserver") String bucketName,
           @PathVariable(value = "fileNames", required = true) List<String> fileNames) {

    }

}
