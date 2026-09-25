/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 验证交接、迁移与审计在权限和失败路径上可用。商业咨询微信：zhuatech / zhuatech2。 */
@SpringBootTest @AutoConfigureMockMvc
class CrmDeliveryIntegrationTests {
    @Autowired MockMvc mvc;

    /** 健康端点可供编排探活，且不泄露内部明细。 */
    @Test void healthEndpointReportsReadiness() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    private String login(String username, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.data.token");
    }

    /** 转移后旧销售无权再读取客户、商机、任务和最近跟进；新负责人可继续工作。 */
    @Test void transferMovesRelatedWorkAndKeepsAnAuditTrail() throws Exception {
        String admin = login("admin", "TestAdminPassword-2026");
        String manager = login("manager", "Demo@2026-Local");
        String sales = login("demo", "Demo@2026-Local");
        String targetBody = mvc.perform(post("/api/users").header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"handoff.sales\",\"fullName\":\"交接销售\",\"role\":\"SALES\",\"password\":\"HandoffSecret-2026\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Number targetId = JsonPath.read(targetBody, "$.data.id");
        String target = login("handoff.sales", "HandoffSecret-2026");
        String created = mvc.perform(post("/api/customers").header("Authorization", "Bearer " + sales)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"交接流程专用客户\",\"status\":\"LEAD\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Number customerId = JsonPath.read(created, "$.data.id");
        long id = customerId.longValue();
        mvc.perform(post("/api/opportunities").header("Authorization", "Bearer " + sales)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":" + id + ",\"name\":\"交接商机\",\"amount\":300,\"stage\":\"LEAD\",\"probability\":20}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/tasks").header("Authorization", "Bearer " + sales)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":" + id + ",\"title\":\"交接任务\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/follow-ups").header("Authorization", "Bearer " + sales)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":" + id + ",\"method\":\"PHONE\",\"content\":\"交接前沟通\"}"))
                .andExpect(status().isOk());

        String transfer = "{\"ownerId\":" + targetId.longValue() + ",\"reason\":\"销售工作交接\"}";
        mvc.perform(patch("/api/customers/" + id + "/owner").header("Authorization", "Bearer " + sales)
                .contentType(MediaType.APPLICATION_JSON).content(transfer)).andExpect(status().isForbidden());
        mvc.perform(patch("/api/customers/" + id + "/owner").header("Authorization", "Bearer " + manager)
                .contentType(MediaType.APPLICATION_JSON).content(transfer)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ownerName").value("交接销售"));
        mvc.perform(get("/api/customers/" + id).header("Authorization", "Bearer " + sales)).andExpect(status().isForbidden());
        mvc.perform(get("/api/customers/" + id).header("Authorization", "Bearer " + target)).andExpect(status().isOk());
        mvc.perform(get("/api/opportunities").header("Authorization", "Bearer " + target))
                .andExpect(jsonPath("$.data[*].name", hasItem("交接商机")));
        mvc.perform(get("/api/tasks").header("Authorization", "Bearer " + target))
                .andExpect(jsonPath("$.data[*].title", hasItem("交接任务")));
        mvc.perform(get("/api/follow-ups/recent").header("Authorization", "Bearer " + sales))
                .andExpect(content().string(not(containsString("交接前沟通"))));
        mvc.perform(get("/api/audit-events").header("Authorization", "Bearer " + sales)).andExpect(status().isForbidden());
        mvc.perform(get("/api/audit-events").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data[*].action", hasItem("CUSTOMER_TRANSFER")));
    }

    /** 导入遇到无效行整批不写入；模板、导出和重复校验可实际使用。 */
    @Test void csvImportIsAtomicAndExportIsSpreadsheetSafe() throws Exception {
        String admin = login("admin", "TestAdminPassword-2026");
        String sales = login("demo", "Demo@2026-Local");
        mvc.perform(get("/api/customers/export").header("Authorization", "Bearer " + sales)).andExpect(status().isForbidden());
        String template = mvc.perform(get("/api/customers/template").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String bad = template + "\"导入回滚客户\",,,,,,,,,,,demo\r\n" + "\"无效负责人客户\",,,,,,,,,,,missing.user\r\n";
        MockMultipartFile badFile = new MockMultipartFile("file", "bad.csv", "text/csv", bad.getBytes(StandardCharsets.UTF_8));
        mvc.perform(multipart("/api/customers/import").file(badFile).header("Authorization", "Bearer " + admin))
                .andExpect(status().isBadRequest());
        MockMultipartFile oversized = new MockMultipartFile("file", "large.csv", "text/csv", new byte[1_000_001]);
        mvc.perform(multipart("/api/customers/import").file(oversized).header("Authorization", "Bearer " + admin))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("请选择不超过 1 MB 的 UTF-8 CSV 文件"));
        mvc.perform(get("/api/customers").param("keyword", "导入回滚客户").header("Authorization", "Bearer " + admin))
                .andExpect(jsonPath("$.data.length()").value(0));

        String good = template + "\"导入,引号\"\"客户\",,,,,,,,,,,demo\r\n";
        MockMultipartFile goodFile = new MockMultipartFile("file", "good.csv", "text/csv", good.getBytes(StandardCharsets.UTF_8));
        mvc.perform(multipart("/api/customers/import").file(goodFile).header("Authorization", "Bearer " + sales))
                .andExpect(status().isForbidden());
        mvc.perform(multipart("/api/customers/import").file(goodFile).header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(1));
        mvc.perform(multipart("/api/customers/import").file(goodFile).header("Authorization", "Bearer " + admin))
                .andExpect(status().isBadRequest());
        String csv = mvc.perform(get("/api/customers/export").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        org.junit.jupiter.api.Assertions.assertTrue(csv.contains("\"导入,引号\"\"客户\""));

        mvc.perform(post("/api/customers").header("Authorization", "Bearer " + sales)
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"=2+2\",\"status\":\"LEAD\"}"))
                .andExpect(status().isOk());
        String safeCsv = mvc.perform(get("/api/customers/export").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        org.junit.jupiter.api.Assertions.assertTrue(safeCsv.contains("\"'=2+2\""));
        mvc.perform(get("/api/audit-events").header("Authorization", "Bearer " + admin))
                .andExpect(jsonPath("$.data[*].action", hasItem("CUSTOMER_IMPORT")));
    }
}
