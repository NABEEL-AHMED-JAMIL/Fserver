package com.ballistic.fserver.restapi;
//https://stackoverflow.com/questions/21800726/using-spring-mvc-test-to-unit-test-multipart-post-request
import com.ballistic.fserver.dto.AccountBean;
import com.ballistic.fserver.exception.FileStorageException;
import com.ballistic.fserver.exception.IllegalBeanFieldException;
import com.ballistic.fserver.exception.IllegalFileFormatException;
import com.ballistic.fserver.manager.FileStoreManager;
import com.ballistic.fserver.manager.ManagerResponse;
import com.ballistic.fserver.pojo.Account;
import com.ballistic.fserver.pojo.FileInfo;
import com.ballistic.fserver.service.FileStoreService;
import com.ballistic.fserver.service.IAccountService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import static com.ballistic.fserver.validation.ImageFileValidator.isSupportedContentType;
import static com.ballistic.fserver.validation.StatusValidator.isSupportedStatusType;

/**
 * Note:- handle home-page api
 * */
@RestController
@RequestMapping(value = "/api")
@Api(value="FServer Management System API", description="Operations on Server.")
public class FLocalRestController {

    private static final Logger logger = LogManager.getLogger(FLocalRestController.class);

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

    /**
     * Done test 99.99%
     * Note:- Possible Test case for this api are follow
     * Check the server running or not
     * */
    @ApiOperation(value = "Verify Server Active or Not", response = String.class)
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    public String pingPong() {
        logger.info("server-active");
        return "pong";
    }

    /**
     * Done test 99.99%
     * Note:- Possible Test case for this api are follow
     * 1) Test api with valid input(png,jpg) type image => pass will response 200 with object
     * 2) Test api with blank @RequestParam will throw error => fail will response 400 with error object
     * 3) Test api with non valid input(not image) will => fail response 400 with error object
     * */
    @ApiOperation(value = "File upload with single", response = APIResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "The POST call is Successful"),
        @ApiResponse(code = 500, message = "The POST call is Failed"),
        @ApiResponse(code = 404, message = "The API could not be found")
    })
    @RequestMapping(value = "/save/file/local", method = RequestMethod.POST)
    public ResponseEntity<APIResponse> uploadFile(@RequestParam(name = "file", required = true) MultipartFile file)
            throws IllegalFileFormatException, FileStorageException {
        long sTime = System.nanoTime();
        logger.debug("upload-single file");
        if(isFileValidType(file.getContentType())) {
            logger.debug("file content accept " + file.getContentType());
            this.apiResponse = fileProcess(file, sTime);
        }
        logger.info("total response time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
        return new ResponseEntity<APIResponse>(this.apiResponse, HttpStatus.OK);
    }

    /**
     * Done test 99.99%
     * Note:- Possible Test case for this api are follow
     * 1) Test api with valid input(png,jpg) type image => pass will response 200 with object
     * 2) Test api with blank @RequestParam will throw error => fail will response 400 with error object
     * 3) Test api with non valid input(not image) will => fail response 400 with error object
     * */
    @ApiOperation(value = "Multiple Files upload")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The POST call is Successful"),
            @ApiResponse(code = 500, message = "The POST call is Failed"),
            @ApiResponse(code = 404, message = "The API could not be found")
    })
    @RequestMapping(value = "/save/files/local", method = RequestMethod.POST)
    public ResponseEntity<List<APIResponse<?>>> uploadFiles(@RequestParam(value = "files", required = true) List<MultipartFile> files)
            throws IllegalFileFormatException, FileStorageException {
        long sTime = System.nanoTime();
        logger.info("upload-multiple files");
        if(files.isEmpty()) {
            logger.error("filename contains invalid length {} ", files.size());
            HashMap<String, Object> error = new HashMap<String, Object>();
            error.put("message", "Sorry! Filename contains invalid length " + files.size());
            error.put("status", HttpStatus.BAD_REQUEST);
            throw new FileStorageException(String.valueOf(error));
        }
        // Note :- 'here this upload file dto help to collect the non-support file info'
        List<String> wrongTypeFile = files.parallelStream().filter(file -> {
            logger.debug("file type checking process..");
            return !isSupportedContentType(file.getContentType());
        }).map(file -> {
            logger.debug("wrong file found :- {}", file.getOriginalFilename());
            return file.getOriginalFilename() + " => " + file.getContentType();
        }).collect(Collectors.toList());

        if(!wrongTypeFile.isEmpty()) {
            logger.error("wrong files :- " + wrongTypeFile.toString());
            throw new IllegalFileFormatException("Wrong file type upload " + wrongTypeFile.toString() + " while required => ", "image/jpeg", "image/png", "image/jpg");
        }

        this.apiResponses = new ArrayList<>();
        // file store's one by one
        files.stream().forEach(file -> {
            logger.debug("--------------------------");
            this.apiResponses.add(fileProcess(file, sTime));
        });
        logger.info("total response time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");

        return new ResponseEntity<>(this.apiResponses, HttpStatus.OK);
    }

    /**
     * Done test 99.99%
     * Note:- Possible Test case for this api are follow
     * 1) Test api with valid @RequestParam default Account info => pass will response 200 with object
     * 2) Test api with blank @RequestParam will throw error => fail will response 400 with error object
     * 3) Test api with non valid input(not image) will => fail response 400 with error object
     * */
    @ApiOperation(value = "File upload with object field", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/save/account/local", method = RequestMethod.POST)
    public ResponseEntity<APIResponse<?>> saveAccountWithSingleFile(
            @ApiParam(name = "account", value = "Select File with Dto field", required = true)
            @Valid AccountBean accountBean, BindingResult bindingResult) throws IllegalBeanFieldException {
        long sTime = System.nanoTime();
        logger.info("save file with account process");
        if(bindingResult.hasErrors()) {
            AtomicInteger bind = new AtomicInteger();
            HashMap<String, HashMap<String, Object>> error = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                String key = "key-"+bind;
                HashMap<String, Object> value = new HashMap<>();
                value.put("objectName", fieldError.getObjectName());
                value.put("field", !StringUtils.isEmpty(fieldError.getField()) ? fieldError.getField() : "null");
                value.put("reject", ObjectUtils.anyNotNull(fieldError.getRejectedValue()) ? fieldError.getRejectedValue() : "null");
                value.put("message", fieldError.getDefaultMessage());
                error.put(key, value);
                bind.getAndIncrement();
            });
            throw new IllegalBeanFieldException(error.toString());
        }
        this.apiResponse = this.uploadFile(accountBean.getFile()).getBody();
        logger.debug("account file save time " + ((System.nanoTime() - sTime) / 1000000) + ".ms");

        if(this.apiResponse.getReturnCode().equals(HttpStatus.OK)) {
            // add-info to the account
            Account account = new Account();
            account.setEmail(accountBean.getEmail());
            account.setPassword(accountBean.getPassword());
			account.setFileInfo((FileInfo) this.apiResponse.getEntity());
            account = this.iAccountService.saveAccount(account);
            logger.info("account with file single file info save time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
            this.apiResponse = new APIResponse<Account>("File save with account", HttpStatus.OK, account);
            logger.info("account added " + this.apiResponse.toString());
            logger.info("total response time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
       }

        return new ResponseEntity<APIResponse<?>>(this.apiResponse, HttpStatus.OK);
    }


    /**
     * Done test 99.99%
     * Note:- Possible Test case for this api are follow
     * 1) Test api with valid @RequestParam default Accounts info => pass will response 200 with object
     * 2) Test api with blank @RequestParam will throw error => fail will response 400 with error object
     * 3) Test api with non valid input(not image) will => fail response 400 with error object
     * */
    @ApiOperation(value = "Files upload with List<object> fields", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/save/accounts/local", method = RequestMethod.POST)
    public ResponseEntity<List<APIResponse<?>>> saveAccountsWithFile(
            @ApiParam(name = "account", value = "Select Files with Dto field", required = true)
            @Valid List<AccountBean> accountBeans) {

        List<AccountBean> accountWithWrongFile = accountBeans.parallelStream().
            filter(accountBean -> {
               return !isSupportedContentType(accountBean.getFile().getContentType());
            }).collect(Collectors.toList());

        if(!accountWithWrongFile.isEmpty()) {
            logger.error("wrong Account's" + accountWithWrongFile.toString());
            throw new IllegalFileFormatException("Account with Wrong file type upload " +
                    accountWithWrongFile.toString() + " while required => ", "image/jpeg", "image/png", "image/jpg");
        }

        this.apiResponses = new ArrayList<>();
        accountBeans.stream().forEach(accountBean -> {
            logger.debug("account's save process...");
            this.apiResponse = this.saveAccountWithSingleFile(accountBean, null).getBody();
            this.apiResponses.add(this.apiResponse);
        });
        logger.info("accounts added with file" + this.apiResponse.toString());
        logger.info("response :-" + this.apiResponse.getEntity().toString() + " account's save successfully with single file store successfully");
        return new ResponseEntity<List<APIResponse<?>>>(this.apiResponses, HttpStatus.OK);
    }

    /**
     * Done test 99.99%
     * Note:- Possible Test case for this api are follow
     * 1) Test api with valid @PathVariable default status info => pass will response 200 with object
     * 2) Test api with blank @PathVariable will throw error => fail will response 400 with error object
     * */
    @ApiOperation(value = "Find Accounts by status", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Account.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @Async("asyncExecutor")
    @RequestMapping(value = "/fetch/accounts/local/status={status}", method = RequestMethod.GET)
    public CompletableFuture<ResponseEntity<?>> fetchAllAccountByStatus(@NotBlank @NotBlank @PathVariable(name = "status") String status)
            throws InterruptedException {
        try {
            long sTime = System.nanoTime();
            if(isStatusValidType(status)) {
                logger.debug("status accept " + status);
                this.apiResponse = new APIResponse<>("Fetch Record", HttpStatus.OK, this.iAccountService.fetchAllAccountByStatus(status));
                Thread.sleep(200L);
                logger.debug("fetch response time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
            }
            logger.info("account fetch " + this.apiResponse.toString());
            logger.info("total response time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
            return CompletableFuture.completedFuture(new ResponseEntity<APIResponse>(this.apiResponse, HttpStatus.OK));
        } catch (InterruptedException ex) {
            throw new InterruptedException("Thread Interrupted while retrieve image");
        }
    }

    /**
     * Done test 99.99%
     * Note:- Possible Test case for this api are follow
     * 1) Test api with valid @PathVariable default id info => pass will response 200 with object
     * 2) Test api with blank @PathVariable will throw error => fail will response 400 with error object
     * */
    @ApiOperation(value = "Find Account by id", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Account.class),
           @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/fetch/accounts/accountId={id}", method = RequestMethod.GET)
    public ResponseEntity<?> fetchAccountById(@NotBlank @NotBlank @PathVariable(name = "id", required = true) String id) {
        long sTime = System.nanoTime();
        this.apiResponse = new APIResponse<>("Fetch by Account ID", HttpStatus.OK, this.iAccountService.fetchAccountById(id));
        logger.info("account fetch " + this.apiResponse.toString());
        logger.info("total response time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
        return new ResponseEntity<APIResponse>(this.apiResponse, HttpStatus.OK);
    }

    /**
     * Note:- use pagination --> Next Step
     */
    /**
     * Done test 88.99% ==> pagination not imp yet
     * Note:- Possible Test case for this api are follow
     * 1) Test api with valid @PathVariable default id info => pass will response 200 with object
     * 2) Test api with blank @PathVariable will throw error => fail will response 400 with error object
     * */
    @ApiOperation(value = "Find Accounts", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Account.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @Async("asyncExecutor")
    @RequestMapping(value = "/fetch/accounts/local", method = RequestMethod.GET)
    public CompletableFuture<ResponseEntity<?>> fetchAllAccounts() throws InterruptedException {

        try {
            long sTime = System.nanoTime();
            this.apiResponse = new APIResponse<>("Fetch All Account's", HttpStatus.OK, this.iAccountService.fetchAllAccount());
            Thread.sleep(200L);
            logger.info("account fetch " + this.apiResponse.toString());
            logger.info("total response time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
            return CompletableFuture.completedFuture(new ResponseEntity<APIResponse>(this.apiResponse, HttpStatus.OK));
        } catch (InterruptedException ex) {
            throw new InterruptedException("Thread Interrupted while retrieve image");
        }
    }

    /**
     * Done test 99.99%
     * Note:- Possible Test case for this api are follow
     * 1) Test api with valid @PathVariable default id info => pass will response 200 with object
     * 2) Test api with blank @PathVariable will throw error => fail will response 400 with error object
     * */
    @ApiOperation(value = "Delete Account", response = APIResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Account.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/delete/account/local/accountId={id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAccount(@PathVariable(name = "id", required = true) String id) throws NullPointerException {
        long sTime = System.nanoTime();
        this.apiResponse = (APIResponse<?>) this.fetchAccountById(id).getBody();
        if(ObjectUtils.anyNotNull(this.apiResponse) && this.apiResponse.getReturnCode().equals(HttpStatus.OK)) {
            Account account = (Account) this.apiResponse.getEntity();
            // here bz some of status are null early data be
            if(ObjectUtils.anyNotNull(account) && (account.getStatus().equalsIgnoreCase("Save") || account.getStatus() == null)) {
                // change the status...
                if(ObjectUtils.anyNotNull(account.getFileInfo())) {
                    /**
                     * Note:- We change the status in db so we are not delete the file from our Bucket
                     * set the account file info as new update file info
                     * */
                    account.setFileInfo(this.ifileStoreService.deleteFile(account.getFileInfo()));
                }
                // now send this info into the service for change the status of
                account = this.iAccountService.deleteAccount(account);
                this.apiResponse = new APIResponse<>("Fetch All Account's", HttpStatus.OK, account);
                logger.debug("account delte " + this.apiResponse.toString());
            } else {
                // if the status of Account not "Save" && "null" then here
                throw new NullPointerException("Account Not Found In Db");
            }
        } else {
            // mean db already have status="Delete"
            throw new NullPointerException("Account Not Found In Db");
        }
        logger.info("total response time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
        return new ResponseEntity<APIResponse>(this.apiResponse, HttpStatus.OK);

    }

    /**
     * Call:-http://localhost:8080/public/test/1,2,3,4
     * */
    @ApiOperation(value = "Find Accounts")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Account.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/delete/accounts/local/accountId={ids}", method = RequestMethod.DELETE)
    public List<String> deleteAccounts(@NotBlank @NotNull  @PathVariable(name = "ids", required = true) List<String> ids) {
        if(ids.isEmpty()) {
            throw new NullPointerException("Ids list empty it's should be => http://localhost:8080/demo/test/1,2,3,4");
        }
        // oky first update the account then
        return ids;
    }

    // done test 99.99%
    // will be thread base request take time and high request
    // Note:- use the cache and cache will remove after mint
    @ApiOperation(value = "Find Accounts")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Account.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @Async("asyncExecutor")
    @RequestMapping(value = "/download/image/file-name={fileName}", method = RequestMethod.GET)
    public CompletableFuture<ResponseEntity<?>> readeFile(@PathVariable(name = "fileName", required = true) String fileName, HttpServletRequest request)
            throws IOException, InterruptedException {
        long sTime = System.nanoTime();
        try {
            try {
                Resource resource = this.fileStoreManager.getLocalFileStoreManager().loadFileAsResource(fileName);
                Thread.sleep(200L);    //Intentional delay
                logger.info("file receive " + resource.toString());
                // Try to determine file's content type
                String contentType = null;
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                if(StringUtils.isEmpty(contentType)) {
                    contentType = "application/octet-stream";
                    logger.info("content type " + contentType);
                }
                logger.info("total response time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
                return CompletableFuture.completedFuture(ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource));
            } catch (InterruptedException ex) {
                throw new InterruptedException("Thread Interrupted while retrieve image");
            }
        } catch (IOException ex) {
            // logger.error("Error :- " + "Message :- " + ex.getMessage() + ", Local Message :- " + ex.getLocalizedMessage());
            logger.info("total response time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
            throw new IOException(ex.getMessage());

        }
    }

    // done-test 99.99%
    private APIResponse<?> fileProcess(MultipartFile file, long sTime) {

        this.managerResponse = this.fileStoreManager.getLocalFileStoreManager().storeFile(file);
        if(ObjectUtils.anyNotNull(this.managerResponse) && this.managerResponse.getReturnCode().equals(HttpStatus.OK)) {
            //FileInfo fileInfo = (FileInfo) this.managerResponse.getEntity();
            FileInfo fileInfo = this.modelMapper.map(this.managerResponse.getEntity(), FileInfo.class);
            logger.info("file upload time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
            fileInfo = this.ifileStoreService.storeFile(fileInfo);
            logger.info("file data-store time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
            this.apiResponse = new APIResponse<FileInfo>("File Store successfully ", HttpStatus.OK, fileInfo);
            logger.info("response :-" + this.apiResponse.getEntity().toString() + " file store successfully");
        }

        return this.apiResponse;
    }

    // done-test 99.99%
    private Boolean isFileValidType(String contentType) throws IllegalBeanFieldException {
        if(!isSupportedContentType(contentType)) {
            // sorry repository store support only the image's
            logger.info("file content reject " + contentType);
            throw new IllegalFileFormatException("Wrong file type upload " + contentType + " while required => ", "image/jpeg", "image/png", "image/jpg");
        }
        return true;
    }

    // done-test 99.99%
    private Boolean isStatusValidType(String statusType) throws IllegalBeanFieldException {
        if(!isSupportedStatusType(statusType)) {
            // sorry repository store support only the image's
            logger.info("Status reject " + statusType);
            throw new IllegalFileFormatException("Only \"Save\" or \"Delete\" Status Use");
        }
        return true;
    }

}
