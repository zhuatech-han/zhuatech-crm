/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 验证账号管理权限、密码轮换与旧令牌撤销。商业咨询微信：zhuatech / zhuatech2。 */
@SpringBootTest @AutoConfigureMockMvc
class UserAccountIntegrationTests {
    @Autowired MockMvc mvc;

    private String login(String username, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.data.token");
    }

    @Test void adminCanManageUsersAndRevokesPreviousTokens() throws Exception {
        String admin = login("admin", "TestAdminPassword-2026");
        String sales = login("demo", "Demo@2026-Local");
        mvc.perform(get("/api/users").header("Authorization", "Bearer " + sales)).andExpect(status().isForbidden());
        mvc.perform(post("/api/users").header("Authorization", "Bearer " + sales).contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"unit.sales\",\"fullName\":\"测试销售\",\"role\":\"SALES\",\"password\":\"InitialSecret-2026\"}"))
                .andExpect(status().isForbidden());

        String created = mvc.perform(post("/api/users").header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"unit.sales\",\"fullName\":\"测试销售\",\"role\":\"SALES\",\"password\":\"InitialSecret-2026\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.password").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        Number id = JsonPath.read(created, "$.data.id");
        String original = login("unit.sales", "InitialSecret-2026");
        mvc.perform(patch("/api/users/" + id.longValue() + "/password").header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"password\":\"ReplacementSecret-2026\"}"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + original)).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"unit.sales\",\"password\":\"InitialSecret-2026\"}"))
                .andExpect(status().isUnauthorized());
        String replacement = login("unit.sales", "ReplacementSecret-2026");
        mvc.perform(patch("/api/users/" + id.longValue() + "/enabled").header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"enabled\":false}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.enabled").value(false));
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + replacement)).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"unit.sales\",\"password\":\"ReplacementSecret-2026\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(patch("/api/users/" + id.longValue() + "/enabled").header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"enabled\":true}"))
                .andExpect(status().isOk());
        login("unit.sales", "ReplacementSecret-2026");
        mvc.perform(patch("/api/users/1/enabled").header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"enabled\":false}"))
                .andExpect(status().isBadRequest());
    }

    @Test void userCanChangeOwnPasswordButNotReuseIt() throws Exception {
        String admin = login("admin", "TestAdminPassword-2026");
        mvc.perform(post("/api/users").header("Authorization", "Bearer " + admin).contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"self.change\",\"fullName\":\"改密测试\",\"role\":\"SALES\",\"password\":\"OriginalSecret-2026\"}"))
                .andExpect(status().isOk());
        String oldToken = login("self.change", "OriginalSecret-2026");
        mvc.perform(post("/api/auth/change-password").header("Authorization", "Bearer " + oldToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"currentPassword\":\"wrong\",\"newPassword\":\"AnotherSecret-2026\"}"))
                .andExpect(status().isBadRequest());
        String changed = mvc.perform(post("/api/auth/change-password").header("Authorization", "Bearer " + oldToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"currentPassword\":\"OriginalSecret-2026\",\"newPassword\":\"AnotherSecret-2026\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String newToken = JsonPath.read(changed, "$.data.token");
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + oldToken)).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + newToken)).andExpect(status().isOk());
        mvc.perform(post("/api/auth/change-password").header("Authorization", "Bearer " + newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"currentPassword\":\"AnotherSecret-2026\",\"newPassword\":\"AnotherSecret-2026\"}"))
                .andExpect(status().isBadRequest());
    }
}
