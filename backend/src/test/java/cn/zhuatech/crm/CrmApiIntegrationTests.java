/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@SpringBootTest @AutoConfigureMockMvc
class CrmApiIntegrationTests {
    @Autowired MockMvc mvc;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void salesUserCanLoginAndReadOwnCustomers() throws Exception {
        String body=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"demo\",\"password\":\"Demo@2026-Local\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.user.role").value("SALES")).andReturn().getResponse().getContentAsString();
        String token=JsonPath.read(body,"$.data.token");
        mvc.perform(get("/api/customers").header("Authorization","Bearer "+token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray()).andExpect(jsonPath("$.data[0].ownerName").value("演示销售"));
    }

    /** 完成客户、联系人、商机、跟进和任务的核心链路。商业咨询微信：zhuatech / zhuatech2。 */
    @Test void salesUserCanCompleteCoreCustomerWorkflow() throws Exception {
        String login=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"demo\",\"password\":\"Demo@2026-Local\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String token=JsonPath.read(login,"$.data.token");
        String customer=mvc.perform(post("/api/customers").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"集成测试客户\",\"level\":\"B\",\"status\":\"LEAD\",\"source\":\"自动化测试\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.name").value("集成测试客户")).andReturn().getResponse().getContentAsString();
        Number customerId=JsonPath.read(customer,"$.data.id");
        mvc.perform(post("/api/contacts").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
            .content("{\"customerId\":"+customerId.longValue()+",\"name\":\"集成测试联系人\",\"primaryContact\":true}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.name").value("集成测试联系人"));
        String opportunity=mvc.perform(post("/api/opportunities").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
            .content("{\"customerId\":"+customerId.longValue()+",\"name\":\"集成测试商机\",\"amount\":10000,\"stage\":\"LEAD\",\"probability\":10}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.name").value("集成测试商机"))
            .andReturn().getResponse().getContentAsString();
        Number opportunityId=JsonPath.read(opportunity,"$.data.id");
        mvc.perform(post("/api/follow-ups").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
            .content("{\"customerId\":"+customerId.longValue()+",\"opportunityId\":"+opportunityId.longValue()+",\"method\":\"PHONE\",\"content\":\"完成首次需求沟通\",\"nextAction\":\"发送产品资料\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.customerName").value("集成测试客户"))
            .andExpect(jsonPath("$.data.opportunityName").value("集成测试商机"));
        mvc.perform(post("/api/tasks").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
            .content("{\"customerId\":"+customerId.longValue()+",\"title\":\"集成测试任务\",\"priority\":\"HIGH\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.title").value("集成测试任务"));

        mvc.perform(get("/api/customers/"+customerId.longValue()).header("Authorization","Bearer "+token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.name").value("集成测试客户"));
        mvc.perform(get("/api/contacts").param("customerId",String.valueOf(customerId.longValue()))
            .header("Authorization","Bearer "+token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data[*].name",hasItem("集成测试联系人")));
        mvc.perform(get("/api/opportunities").param("customerId",String.valueOf(customerId.longValue()))
            .header("Authorization","Bearer "+token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data[*].name",hasItem("集成测试商机")));
        mvc.perform(get("/api/follow-ups").param("customerId",String.valueOf(customerId.longValue()))
            .header("Authorization","Bearer "+token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data[*].content",hasItem("完成首次需求沟通")));
        mvc.perform(get("/api/tasks").header("Authorization","Bearer "+token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data[*].title",hasItem("集成测试任务")));
    }

    /**
     * 销售人员不能读取经理创建的客户及其联系人。
     * 商业授权咨询微信：zhuatech / zhuatech2。
     */
    @Test void salesUserCannotReadAnotherOwnersCustomer() throws Exception {
        String managerLogin=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"manager\",\"password\":\"Demo@2026-Local\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String managerToken=JsonPath.read(managerLogin,"$.data.token");
        String customer=mvc.perform(post("/api/customers").header("Authorization","Bearer "+managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"经理专属测试客户\",\"level\":\"B\",\"status\":\"LEAD\",\"source\":\"自动化测试\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Number customerId=JsonPath.read(customer,"$.data.id");

        String salesLogin=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"demo\",\"password\":\"Demo@2026-Local\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String salesToken=JsonPath.read(salesLogin,"$.data.token");
        mvc.perform(get("/api/customers/"+customerId.longValue()).header("Authorization","Bearer "+salesToken))
            .andExpect(status().isForbidden()).andExpect(jsonPath("$.message").value("没有操作权限"));
        mvc.perform(get("/api/contacts").param("customerId",String.valueOf(customerId.longValue()))
            .header("Authorization","Bearer "+salesToken))
            .andExpect(status().isForbidden());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void unauthenticatedRequestsAreRejected() throws Exception {
        mvc.perform(get("/api/dashboard"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("请先登录或重新登录"));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void invalidTokenReturnsStructuredUnauthorizedResponse() throws Exception {
        mvc.perform(get("/api/dashboard").header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("请先登录或重新登录"));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void unsupportedMethodAndMalformedJsonUseClientErrorStatus() throws Exception {
        mvc.perform(get("/api/auth/login"))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.message").value("请求方法不支持"));
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{bad-json"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("请求体格式不正确"));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void salesUserCanEvaluateCustomerHealth() throws Exception {
        String login=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"demo\",\"password\":\"Demo@2026-Local\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String token=JsonPath.read(login,"$.data.token");
        mvc.perform(post("/api/customer-intelligence/health-score").header("Authorization","Bearer "+token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerName\":\"华东示例客户\",\"engagementScore\":70,\"paymentRisk\":0.6,\"openOpportunities\":2,\"inactiveDays\":20,\"criticalComplaint\":true}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.healthScore").value(9))
            .andExpect(jsonPath("$.data.band").value("RISK"))
            .andExpect(jsonPath("$.data.managerReview").value(true));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void salesUserCanGenerateWeightedOpportunityForecast() throws Exception {
        String login=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"demo\",\"password\":\"Demo@2026-Local\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String token=JsonPath.read(login,"$.data.token");
        mvc.perform(post("/api/customer-intelligence/opportunity-forecast").header("Authorization","Bearer "+token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quarterTarget\":1000000,\"deals\":[{\"name\":\"华东数字化项目\",\"amount\":800000,\"probability\":75,\"stage\":\"NEGOTIATION\",\"expectedCloseDate\":\"2026-08-18\",\"daysSinceActivity\":3,\"criticalBlocker\":false},{\"name\":\"门店升级项目\",\"amount\":300000,\"probability\":40,\"stage\":\"PROPOSAL\",\"expectedCloseDate\":\"2026-08-12\",\"daysSinceActivity\":18,\"criticalBlocker\":true}]}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.weightedForecast").value(720000.0))
            .andExpect(jsonPath("$.data.commitForecast").value(600000.0))
            .andExpect(jsonPath("$.data.atRiskDeals").value(1))
            .andExpect(jsonPath("$.data.deals[0].name").value("门店升级项目"));
    }
}
