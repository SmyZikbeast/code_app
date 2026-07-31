package Resources;

import Enums.Code;

public class RepositoryResponse<T> {
    Code responseCode;
    T response = null;
    public RepositoryResponse(Code responseCode){
        this.responseCode = responseCode;
    }
    public RepositoryResponse(Code responseCode, T response){
        this.responseCode = responseCode;
        this.response = response;
    }
    public T getBody(){
        return this.response;
    }
    public Code getCode(){
        return this.responseCode;
    }
}
