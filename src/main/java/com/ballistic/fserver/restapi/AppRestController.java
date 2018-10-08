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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import static com.ballistic.fserver.validation.ImageFileValidator.isSupportedContentType;

/**
 * Note:- handle home-page api
 * */
@RestController
@RequestMapping(value = "/api")
@Api(value="Server Management System API", description="Operations on Server.")
public class AppRestController {

    private static final Logger logger = LogManager.getLogger(AppRestController.class);

    private APIResponse<?> apiResponse = null;
    private List<APIResponse<?>> apiResponses = null;
    private ManagerResponse<?> managerResponse = null;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileStoreManager fileStoreManager;
    @Autowired
    private FileStoreService fileStoreService;
    @Autowired
    private IAccountService iAccountService;

    // done test 100%
    @ApiOperation(value = "Verify Server Active or Not", response = String.class)
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    public String pingPong() {
        logger.info("server-active");
        return "pong";
    }

    // done test 99.99%
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
        logger.info("upload-single file");
        if(isFileValidType(file.getContentType())) {
            logger.info("file content accept " + file.getContentType());
            // call method
            this.apiResponse = fileProcess(file, sTime);
        }
        logger.info("total response time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
        return new ResponseEntity<APIResponse>(this.apiResponse, HttpStatus.OK);
    }


    // done test 99.99%
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
        List<String> wrongTypeFile = files.stream().
            filter(file -> {
                logger.debug("file type checking process..");
                return !isSupportedContentType(file.getContentType());
            }).map(file -> {
            logger.debug("wrong file found :- {}", file.getOriginalFilename());
            return file.getOriginalFilename() + " => " + file.getContentType();
        }).collect(Collectors.toList());

        if(!wrongTypeFile.isEmpty()) {
            logger.error("wrong files :- " + wrongTypeFile.toString());
            throw new IllegalFileFormatException("Wrong file type upload " + wrongTypeFile.toString() +
                    " while required => ", "image/jpeg", "image/png", "image/jpg");
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

    // done test 99.99%
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
                value.put("field", fieldError.getField() != null ? fieldError.getField() : "null");
                value.put("reject", fieldError.getRejectedValue() != null ? fieldError.getRejectedValue() : "null");
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


    // done test 00.99%
    @ApiOperation(value = "Files upload with List<object> fields", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/save/accounts/local", method = RequestMethod.POST)
    public ResponseEntity<List<APIResponse<?>>> saveAccountsWithFile(
            @ApiParam(name = "account", value = "Select Files with Dto field", required = true)
            List<AccountBean> accountBeans) {

        List<AccountBean> accountWithWrongFile = accountBeans.stream().filter(accountBean -> {
            return !isSupportedContentType(accountBean.getFile().getContentType());
        }).collect(Collectors.toList());

        if(!accountWithWrongFile.isEmpty()) {
            logger.error("wrong Account's" + accountWithWrongFile.toString());
            throw new IllegalFileFormatException("Account with Wrong file type upload " +
                    accountWithWrongFile.toString() + " while required => ", "image/jpeg", "image/png", "image/jpg");
        }

        logger.info("save's account");
        this.apiResponses = new ArrayList<>();
        accountBeans.stream().forEach(accountBean -> {
            this.apiResponse = this.saveAccountWithSingleFile(accountBean, null).getBody();
            this.apiResponses.add(this.apiResponse);
        });
        logger.info("response :-" + this.apiResponse.getEntity().toString() + " account's save successfully with single file store successfully");
        return new ResponseEntity<List<APIResponse<?>>>(this.apiResponses, HttpStatus.OK);
    }

    // done test 00.99%
    @ApiOperation(value = "Find Accounts by status", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Account.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/fetch/accounts/local/status={status}", method = RequestMethod.GET)
    public List<Account> fetchAllAccountByStatus(@PathVariable(name = "status", required = true, value = "save") String status) {


        return null;
    }

    // done test 00.99%
    // handle error
    @ApiOperation(value = "Find Account by id", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Account.class),
           @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/fetch/accounts/accountId={id}", method = RequestMethod.GET)
    public Optional<Account> fetchAccount(@PathVariable(name = "id", required = true) String id) { return null; }

    // handle error
    /**
     * Note:- use pagination
     */
    @ApiOperation(value = "Find Accounts", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Account.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/fetch/accounts/local", method = RequestMethod.GET)
    public List<Account> fetchAllAccounts() {
        return null;
    }

    // handle error
    @ApiOperation(value = "Delete Account", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Account.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/delete/account/local/accountId={id}", method = RequestMethod.DELETE)
    public Account deleteAccount(@PathVariable(name = "id", required = true) String id) {
        return null;
    }

    // handle error
    @ApiOperation(value = "Find Accounts", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Account.class),
            @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/delete/accounts/local", method = RequestMethod.POST)
    public List<Account> deleteAccounts(List<String> ids) {
        return null;
    }

    // done-test 99.99%
    private APIResponse<?> fileProcess(MultipartFile file, long sTime) {

        this.managerResponse = this.fileStoreManager.getLocalFileStoreManager().storeFile(file);
        if(ObjectUtils.anyNotNull(this.managerResponse) && this.managerResponse.getReturnCode().equals(HttpStatus.OK)) {
            //FileInfo fileInfo = (FileInfo) this.managerResponse.getEntity();
            FileInfo fileInfo = this.modelMapper.map(this.managerResponse.getEntity(), FileInfo.class);
            logger.info("file upload time :- " + ((System.nanoTime() - sTime) / 1000000) + ".ms");
            fileInfo = this.fileStoreService.getLocalFileStoreService().storeFile(fileInfo);
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

}
