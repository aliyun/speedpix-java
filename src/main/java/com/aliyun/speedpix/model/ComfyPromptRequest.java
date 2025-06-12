package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * 创建预测任务的请求
 */
public class ComfyPromptRequest {

    @JsonProperty("workflow_id")
    private String workflowId;

    @JsonProperty("version_id")
    private String versionId;

    @JsonProperty("inputs")
    private Map<String, Object> inputs;

    @JsonProperty("alias_id")
    private String aliasId;

    @JsonProperty("randomise_seeds")
    private Boolean randomiseSeeds;

    @JsonProperty("return_temp_files")
    private Boolean returnTempFiles;

    // Constructors
    public ComfyPromptRequest() {}

    public ComfyPromptRequest(String workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * 创建Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 创建Builder实例并设置workflowId
     */
    public static Builder builder(String workflowId) {
        return new Builder().workflowId(workflowId);
    }

    // Getters and Setters
    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public String getAliasId() {
        return aliasId;
    }

    public void setAliasId(String aliasId) {
        this.aliasId = aliasId;
    }

    public Boolean getRandomiseSeeds() {
        return randomiseSeeds;
    }

    public void setRandomiseSeeds(Boolean randomiseSeeds) {
        this.randomiseSeeds = randomiseSeeds;
    }

    public Boolean getReturnTempFiles() {
        return returnTempFiles;
    }

    public void setReturnTempFiles(Boolean returnTempFiles) {
        this.returnTempFiles = returnTempFiles;
    }

    /**
     * Builder 模式支持链式调用
     */
    public static class Builder {
        private String workflowId;
        private String versionId;
        private Map<String, Object> inputs;
        private String aliasId = "main";
        private Boolean randomiseSeeds;
        private Boolean returnTempFiles;

        public Builder workflowId(String workflowId) {
            this.workflowId = workflowId;
            return this;
        }

        public Builder versionId(String versionId) {
            this.versionId = versionId;
            return this;
        }

        public Builder inputs(Map<String, Object> inputs) {
            this.inputs = inputs;
            return this;
        }

        public Builder aliasId(String aliasId) {
            this.aliasId = aliasId;
            return this;
        }

        public Builder randomiseSeeds(Boolean randomiseSeeds) {
            this.randomiseSeeds = randomiseSeeds;
            return this;
        }

        public Builder returnTempFiles(Boolean returnTempFiles) {
            this.returnTempFiles = returnTempFiles;
            return this;
        }

        public ComfyPromptRequest build() {
            ComfyPromptRequest request = new ComfyPromptRequest();
            request.setWorkflowId(this.workflowId);
            request.setVersionId(this.versionId);
            request.setInputs(this.inputs);
            request.setAliasId(this.aliasId);
            request.setRandomiseSeeds(this.randomiseSeeds);
            request.setReturnTempFiles(this.returnTempFiles);
            return request;
        }
    }
}
