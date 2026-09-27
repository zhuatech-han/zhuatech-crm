/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.dto;

import cn.zhuatech.crm.model.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;

/**
 * 定义客户、联系人、商机、跟进和任务的接口请求与视图结构。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
public final class CrmDto {

    private CrmDto() {}

    /**
     * 封装 CustomerRequest 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record CustomerRequest(
        @NotBlank(message="请输入客户名称") @Size(max=120) String name,
        @Size(max=60) String shortName, @Size(max=60) String industry,
        @Pattern(regexp="A|B|C|D",message="客户等级不正确") String level,
        @NotNull Customer.Status status, @Size(max=40) String source,
        @Size(max=30) String phone, @Email @Size(max=120) String email,
        @Size(max=240) String address, LocalDate nextFollowUpDate, @Size(max=1000) String notes) {}
    /**
     * 客户归属实际转移参数。商业咨询微信：zhuatech / zhuatech2。
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record CustomerTransferRequest(@NotNull Long ownerId, @NotBlank @Size(max=200) String reason) {}
    /**
     * 封装 CustomerView 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record CustomerView(Long id,String name,String shortName,String industry,String level,String status,String source,String phone,String email,String address,LocalDate nextFollowUpDate,String notes,Long ownerId,String ownerName,LocalDateTime createdAt,LocalDateTime updatedAt) {
        /**
         * 将业务实体转换为接口视图，避免返回加密密码等内部字段。
         *
         * Copyright 2026 上海如静知华信息科技有限公司
         * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
         */
        public static CustomerView from(Customer c){return new CustomerView(c.getId(),c.getName(),c.getShortName(),c.getIndustry(),c.getLevel(),c.getStatus().name(),c.getSource(),c.getPhone(),c.getEmail(),c.getAddress(),c.getNextFollowUpDate(),c.getNotes(),c.getOwner().getId(),c.getOwner().getFullName(),c.getCreatedAt(),c.getUpdatedAt());}
    }

    /**
     * 封装 ContactRequest 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record ContactRequest(@NotNull Long customerId,@NotBlank @Size(max=50) String name,@Size(max=60) String title,@Size(max=30) String phone,@Email @Size(max=120) String email,boolean primaryContact,@Size(max=500) String notes) {}
    /**
     * 封装 ContactView 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record ContactView(Long id,Long customerId,String customerName,String name,String title,String phone,String email,boolean primaryContact,String notes) {
        /**
         * 将业务实体转换为接口视图，避免返回加密密码等内部字段。
         *
         * Copyright 2026 上海如静知华信息科技有限公司
         * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
         */
        public static ContactView from(Contact c){return new ContactView(c.getId(),c.getCustomer().getId(),c.getCustomer().getName(),c.getName(),c.getTitle(),c.getPhone(),c.getEmail(),c.isPrimaryContact(),c.getNotes());}
    }

    /**
     * 封装 OpportunityRequest 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record OpportunityRequest(@NotNull Long customerId,@NotBlank @Size(max=120) String name,@NotNull @DecimalMin("0") BigDecimal amount,@NotNull Opportunity.Stage stage,@Min(0) @Max(100) int probability,LocalDate expectedCloseDate,@Size(max=500) String nextStep) {}
    /**
     * 封装 OpportunityStageRequest 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record OpportunityStageRequest(@NotNull Opportunity.Stage stage,@Min(0) @Max(100) int probability,@Size(max=500) String nextStep) {}
    /**
     * 封装 OpportunityView 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record OpportunityView(Long id,Long customerId,String customerName,String name,BigDecimal amount,String stage,int probability,LocalDate expectedCloseDate,String nextStep,String ownerName,LocalDateTime updatedAt) {
        /**
         * 将业务实体转换为接口视图，避免返回加密密码等内部字段。
         *
         * Copyright 2026 上海如静知华信息科技有限公司
         * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
         */
        public static OpportunityView from(Opportunity o){return new OpportunityView(o.getId(),o.getCustomer().getId(),o.getCustomer().getName(),o.getName(),o.getAmount(),o.getStage().name(),o.getProbability(),o.getExpectedCloseDate(),o.getNextStep(),o.getOwner().getFullName(),o.getUpdatedAt());}
    }

    /**
     * 封装 FollowUpRequest 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record FollowUpRequest(@NotNull Long customerId,Long opportunityId,@NotNull FollowUp.Method method,@NotBlank @Size(max=1000) String content,LocalDateTime followUpAt,@Size(max=500) String nextAction,LocalDate nextFollowUpDate) {}
    /**
     * 封装 FollowUpView 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record FollowUpView(Long id,Long customerId,String customerName,Long opportunityId,String opportunityName,String creatorName,String method,String content,LocalDateTime followUpAt,String nextAction,LocalDate nextFollowUpDate) {
        /**
         * 将业务实体转换为接口视图，避免返回加密密码等内部字段。
         *
         * Copyright 2026 上海如静知华信息科技有限公司
         * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
         */
        public static FollowUpView from(FollowUp f){return new FollowUpView(f.getId(),f.getCustomer().getId(),f.getCustomer().getName(),f.getOpportunity()==null?null:f.getOpportunity().getId(),f.getOpportunity()==null?null:f.getOpportunity().getName(),f.getCreator().getFullName(),f.getMethod().name(),f.getContent(),f.getFollowUpAt(),f.getNextAction(),f.getNextFollowUpDate());}
    }

    /**
     * 封装 TaskRequest 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record TaskRequest(Long customerId,@NotBlank @Size(max=120) String title,@Size(max=500) String description,LocalDate dueDate,@Pattern(regexp="LOW|MEDIUM|HIGH") String priority) {}
    /**
     * 封装 TaskStatusRequest 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record TaskStatusRequest(boolean completed) {}
    /**
     * 封装 TaskView 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record TaskView(Long id,Long customerId,String customerName,String title,String description,LocalDate dueDate,String priority,boolean completed) {
        /**
         * 将业务实体转换为接口视图，避免返回加密密码等内部字段。
         *
         * Copyright 2026 上海如静知华信息科技有限公司
         * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
         */
        public static TaskView from(SalesTask t){return new TaskView(t.getId(),t.getCustomer()==null?null:t.getCustomer().getId(),t.getCustomer()==null?null:t.getCustomer().getName(),t.getTitle(),t.getDescription(),t.getDueDate(),t.getPriority(),t.isCompleted());}
    }
    /**
     * 封装 DashboardView 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record DashboardView(long customers,long activeOpportunities,BigDecimal pipelineAmount,long followUpsDue,long pendingTasks) {}
}
