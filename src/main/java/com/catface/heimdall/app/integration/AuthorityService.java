package com.catface.heimdall.app.integration;

import com.catface.bkb.api.authority.AuthorityApi;
import com.catface.bkb.api.authority.request.CheckAuthorityRequest;
import com.catface.common.model.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author catface
 * @since 2022/8/18
 */
@Slf4j
@Service
public class AuthorityService {

    private final AuthorityApi authorityApi;

    public AuthorityService(AuthorityApi authorityApi) {
        this.authorityApi = authorityApi;
    }

    /**
     * 检查当前会话中的用户是否在指定客户下有相应的授权
     *
     * @param ctxClientId   当前会话中的客户ID
     * @param ctxUserId     当前会话中的用户ID
     * @param bizDomainCode 当前请求所属业务域编码,一般是域名
     * @param url           请求的url,除域名,路由前缀,参数之外的完整url
     * @return true:可以访问;false:禁止访问
     */
    public boolean canAccess(Long ctxClientId, Long ctxUserId, String bizDomainCode, String url) {
        CheckAuthorityRequest request = new CheckAuthorityRequest();
        request.setBizDomainCode(bizDomainCode);
        request.setUrl(url);
        request.setClientId(ctxClientId);
        request.setUserId(ctxUserId);
        JsonResult<Boolean> jsonResult = authorityApi.canAccess(request);
        if (jsonResult.getSuccess()) {
            return jsonResult.getData();
        }
        log.warn("调用全校校验接口失败,失败信息:{}", jsonResult.getMessage());
        return false;
    }
}
