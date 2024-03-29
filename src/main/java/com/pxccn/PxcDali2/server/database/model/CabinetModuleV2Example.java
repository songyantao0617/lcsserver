package com.pxccn.PxcDali2.server.database.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CabinetModuleV2Example {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public CabinetModuleV2Example() {
        oredCriteria = new ArrayList<>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andCabinetIdIsNull() {
            addCriterion("cabinet_ID is null");
            return (Criteria) this;
        }

        public Criteria andCabinetIdIsNotNull() {
            addCriterion("cabinet_ID is not null");
            return (Criteria) this;
        }

        public Criteria andCabinetIdEqualTo(Integer value) {
            addCriterion("cabinet_ID =", value, "cabinetId");
            return (Criteria) this;
        }

        public Criteria andCabinetIdNotEqualTo(Integer value) {
            addCriterion("cabinet_ID <>", value, "cabinetId");
            return (Criteria) this;
        }

        public Criteria andCabinetIdGreaterThan(Integer value) {
            addCriterion("cabinet_ID >", value, "cabinetId");
            return (Criteria) this;
        }

        public Criteria andCabinetIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("cabinet_ID >=", value, "cabinetId");
            return (Criteria) this;
        }

        public Criteria andCabinetIdLessThan(Integer value) {
            addCriterion("cabinet_ID <", value, "cabinetId");
            return (Criteria) this;
        }

        public Criteria andCabinetIdLessThanOrEqualTo(Integer value) {
            addCriterion("cabinet_ID <=", value, "cabinetId");
            return (Criteria) this;
        }

        public Criteria andCabinetIdIn(List<Integer> values) {
            addCriterion("cabinet_ID in", values, "cabinetId");
            return (Criteria) this;
        }

        public Criteria andCabinetIdNotIn(List<Integer> values) {
            addCriterion("cabinet_ID not in", values, "cabinetId");
            return (Criteria) this;
        }

        public Criteria andCabinetIdBetween(Integer value1, Integer value2) {
            addCriterion("cabinet_ID between", value1, value2, "cabinetId");
            return (Criteria) this;
        }

        public Criteria andCabinetIdNotBetween(Integer value1, Integer value2) {
            addCriterion("cabinet_ID not between", value1, value2, "cabinetId");
            return (Criteria) this;
        }

        public Criteria andTerminalIndexIsNull() {
            addCriterion("terminal_Index is null");
            return (Criteria) this;
        }

        public Criteria andTerminalIndexIsNotNull() {
            addCriterion("terminal_Index is not null");
            return (Criteria) this;
        }

        public Criteria andTerminalIndexEqualTo(Integer value) {
            addCriterion("terminal_Index =", value, "terminalIndex");
            return (Criteria) this;
        }

        public Criteria andTerminalIndexNotEqualTo(Integer value) {
            addCriterion("terminal_Index <>", value, "terminalIndex");
            return (Criteria) this;
        }

        public Criteria andTerminalIndexGreaterThan(Integer value) {
            addCriterion("terminal_Index >", value, "terminalIndex");
            return (Criteria) this;
        }

        public Criteria andTerminalIndexGreaterThanOrEqualTo(Integer value) {
            addCriterion("terminal_Index >=", value, "terminalIndex");
            return (Criteria) this;
        }

        public Criteria andTerminalIndexLessThan(Integer value) {
            addCriterion("terminal_Index <", value, "terminalIndex");
            return (Criteria) this;
        }

        public Criteria andTerminalIndexLessThanOrEqualTo(Integer value) {
            addCriterion("terminal_Index <=", value, "terminalIndex");
            return (Criteria) this;
        }

        public Criteria andTerminalIndexIn(List<Integer> values) {
            addCriterion("terminal_Index in", values, "terminalIndex");
            return (Criteria) this;
        }

        public Criteria andTerminalIndexNotIn(List<Integer> values) {
            addCriterion("terminal_Index not in", values, "terminalIndex");
            return (Criteria) this;
        }

        public Criteria andTerminalIndexBetween(Integer value1, Integer value2) {
            addCriterion("terminal_Index between", value1, value2, "terminalIndex");
            return (Criteria) this;
        }

        public Criteria andTerminalIndexNotBetween(Integer value1, Integer value2) {
            addCriterion("terminal_Index not between", value1, value2, "terminalIndex");
            return (Criteria) this;
        }

        public Criteria andSiblingIndexIsNull() {
            addCriterion("sibling_Index is null");
            return (Criteria) this;
        }

        public Criteria andSiblingIndexIsNotNull() {
            addCriterion("sibling_Index is not null");
            return (Criteria) this;
        }

        public Criteria andSiblingIndexEqualTo(Integer value) {
            addCriterion("sibling_Index =", value, "siblingIndex");
            return (Criteria) this;
        }

        public Criteria andSiblingIndexNotEqualTo(Integer value) {
            addCriterion("sibling_Index <>", value, "siblingIndex");
            return (Criteria) this;
        }

        public Criteria andSiblingIndexGreaterThan(Integer value) {
            addCriterion("sibling_Index >", value, "siblingIndex");
            return (Criteria) this;
        }

        public Criteria andSiblingIndexGreaterThanOrEqualTo(Integer value) {
            addCriterion("sibling_Index >=", value, "siblingIndex");
            return (Criteria) this;
        }

        public Criteria andSiblingIndexLessThan(Integer value) {
            addCriterion("sibling_Index <", value, "siblingIndex");
            return (Criteria) this;
        }

        public Criteria andSiblingIndexLessThanOrEqualTo(Integer value) {
            addCriterion("sibling_Index <=", value, "siblingIndex");
            return (Criteria) this;
        }

        public Criteria andSiblingIndexIn(List<Integer> values) {
            addCriterion("sibling_Index in", values, "siblingIndex");
            return (Criteria) this;
        }

        public Criteria andSiblingIndexNotIn(List<Integer> values) {
            addCriterion("sibling_Index not in", values, "siblingIndex");
            return (Criteria) this;
        }

        public Criteria andSiblingIndexBetween(Integer value1, Integer value2) {
            addCriterion("sibling_Index between", value1, value2, "siblingIndex");
            return (Criteria) this;
        }

        public Criteria andSiblingIndexNotBetween(Integer value1, Integer value2) {
            addCriterion("sibling_Index not between", value1, value2, "siblingIndex");
            return (Criteria) this;
        }

        public Criteria andModuleTypeIsNull() {
            addCriterion("module_type is null");
            return (Criteria) this;
        }

        public Criteria andModuleTypeIsNotNull() {
            addCriterion("module_type is not null");
            return (Criteria) this;
        }

        public Criteria andModuleTypeEqualTo(String value) {
            addCriterion("module_type =", value, "moduleType");
            return (Criteria) this;
        }

        public Criteria andModuleTypeNotEqualTo(String value) {
            addCriterion("module_type <>", value, "moduleType");
            return (Criteria) this;
        }

        public Criteria andModuleTypeGreaterThan(String value) {
            addCriterion("module_type >", value, "moduleType");
            return (Criteria) this;
        }

        public Criteria andModuleTypeGreaterThanOrEqualTo(String value) {
            addCriterion("module_type >=", value, "moduleType");
            return (Criteria) this;
        }

        public Criteria andModuleTypeLessThan(String value) {
            addCriterion("module_type <", value, "moduleType");
            return (Criteria) this;
        }

        public Criteria andModuleTypeLessThanOrEqualTo(String value) {
            addCriterion("module_type <=", value, "moduleType");
            return (Criteria) this;
        }

        public Criteria andModuleTypeLike(String value) {
            addCriterion("module_type like", value, "moduleType");
            return (Criteria) this;
        }

        public Criteria andModuleTypeNotLike(String value) {
            addCriterion("module_type not like", value, "moduleType");
            return (Criteria) this;
        }

        public Criteria andModuleTypeIn(List<String> values) {
            addCriterion("module_type in", values, "moduleType");
            return (Criteria) this;
        }

        public Criteria andModuleTypeNotIn(List<String> values) {
            addCriterion("module_type not in", values, "moduleType");
            return (Criteria) this;
        }

        public Criteria andModuleTypeBetween(String value1, String value2) {
            addCriterion("module_type between", value1, value2, "moduleType");
            return (Criteria) this;
        }

        public Criteria andModuleTypeNotBetween(String value1, String value2) {
            addCriterion("module_type not between", value1, value2, "moduleType");
            return (Criteria) this;
        }

        public Criteria andModuleNameIsNull() {
            addCriterion("module_name is null");
            return (Criteria) this;
        }

        public Criteria andModuleNameIsNotNull() {
            addCriterion("module_name is not null");
            return (Criteria) this;
        }

        public Criteria andModuleNameEqualTo(String value) {
            addCriterion("module_name =", value, "moduleName");
            return (Criteria) this;
        }

        public Criteria andModuleNameNotEqualTo(String value) {
            addCriterion("module_name <>", value, "moduleName");
            return (Criteria) this;
        }

        public Criteria andModuleNameGreaterThan(String value) {
            addCriterion("module_name >", value, "moduleName");
            return (Criteria) this;
        }

        public Criteria andModuleNameGreaterThanOrEqualTo(String value) {
            addCriterion("module_name >=", value, "moduleName");
            return (Criteria) this;
        }

        public Criteria andModuleNameLessThan(String value) {
            addCriterion("module_name <", value, "moduleName");
            return (Criteria) this;
        }

        public Criteria andModuleNameLessThanOrEqualTo(String value) {
            addCriterion("module_name <=", value, "moduleName");
            return (Criteria) this;
        }

        public Criteria andModuleNameLike(String value) {
            addCriterion("module_name like", value, "moduleName");
            return (Criteria) this;
        }

        public Criteria andModuleNameNotLike(String value) {
            addCriterion("module_name not like", value, "moduleName");
            return (Criteria) this;
        }

        public Criteria andModuleNameIn(List<String> values) {
            addCriterion("module_name in", values, "moduleName");
            return (Criteria) this;
        }

        public Criteria andModuleNameNotIn(List<String> values) {
            addCriterion("module_name not in", values, "moduleName");
            return (Criteria) this;
        }

        public Criteria andModuleNameBetween(String value1, String value2) {
            addCriterion("module_name between", value1, value2, "moduleName");
            return (Criteria) this;
        }

        public Criteria andModuleNameNotBetween(String value1, String value2) {
            addCriterion("module_name not between", value1, value2, "moduleName");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoIsNull() {
            addCriterion("additionInfo is null");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoIsNotNull() {
            addCriterion("additionInfo is not null");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoEqualTo(String value) {
            addCriterion("additionInfo =", value, "additioninfo");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoNotEqualTo(String value) {
            addCriterion("additionInfo <>", value, "additioninfo");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoGreaterThan(String value) {
            addCriterion("additionInfo >", value, "additioninfo");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoGreaterThanOrEqualTo(String value) {
            addCriterion("additionInfo >=", value, "additioninfo");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoLessThan(String value) {
            addCriterion("additionInfo <", value, "additioninfo");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoLessThanOrEqualTo(String value) {
            addCriterion("additionInfo <=", value, "additioninfo");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoLike(String value) {
            addCriterion("additionInfo like", value, "additioninfo");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoNotLike(String value) {
            addCriterion("additionInfo not like", value, "additioninfo");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoIn(List<String> values) {
            addCriterion("additionInfo in", values, "additioninfo");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoNotIn(List<String> values) {
            addCriterion("additionInfo not in", values, "additioninfo");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoBetween(String value1, String value2) {
            addCriterion("additionInfo between", value1, value2, "additioninfo");
            return (Criteria) this;
        }

        public Criteria andAdditioninfoNotBetween(String value1, String value2) {
            addCriterion("additionInfo not between", value1, value2, "additioninfo");
            return (Criteria) this;
        }

        public Criteria andAuditLightCountIsNull() {
            addCriterion("audit_light_count is null");
            return (Criteria) this;
        }

        public Criteria andAuditLightCountIsNotNull() {
            addCriterion("audit_light_count is not null");
            return (Criteria) this;
        }

        public Criteria andAuditLightCountEqualTo(Integer value) {
            addCriterion("audit_light_count =", value, "auditLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditLightCountNotEqualTo(Integer value) {
            addCriterion("audit_light_count <>", value, "auditLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditLightCountGreaterThan(Integer value) {
            addCriterion("audit_light_count >", value, "auditLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditLightCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("audit_light_count >=", value, "auditLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditLightCountLessThan(Integer value) {
            addCriterion("audit_light_count <", value, "auditLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditLightCountLessThanOrEqualTo(Integer value) {
            addCriterion("audit_light_count <=", value, "auditLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditLightCountIn(List<Integer> values) {
            addCriterion("audit_light_count in", values, "auditLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditLightCountNotIn(List<Integer> values) {
            addCriterion("audit_light_count not in", values, "auditLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditLightCountBetween(Integer value1, Integer value2) {
            addCriterion("audit_light_count between", value1, value2, "auditLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditLightCountNotBetween(Integer value1, Integer value2) {
            addCriterion("audit_light_count not between", value1, value2, "auditLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditErrorLightCountIsNull() {
            addCriterion("audit_error_light_count is null");
            return (Criteria) this;
        }

        public Criteria andAuditErrorLightCountIsNotNull() {
            addCriterion("audit_error_light_count is not null");
            return (Criteria) this;
        }

        public Criteria andAuditErrorLightCountEqualTo(Integer value) {
            addCriterion("audit_error_light_count =", value, "auditErrorLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditErrorLightCountNotEqualTo(Integer value) {
            addCriterion("audit_error_light_count <>", value, "auditErrorLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditErrorLightCountGreaterThan(Integer value) {
            addCriterion("audit_error_light_count >", value, "auditErrorLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditErrorLightCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("audit_error_light_count >=", value, "auditErrorLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditErrorLightCountLessThan(Integer value) {
            addCriterion("audit_error_light_count <", value, "auditErrorLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditErrorLightCountLessThanOrEqualTo(Integer value) {
            addCriterion("audit_error_light_count <=", value, "auditErrorLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditErrorLightCountIn(List<Integer> values) {
            addCriterion("audit_error_light_count in", values, "auditErrorLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditErrorLightCountNotIn(List<Integer> values) {
            addCriterion("audit_error_light_count not in", values, "auditErrorLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditErrorLightCountBetween(Integer value1, Integer value2) {
            addCriterion("audit_error_light_count between", value1, value2, "auditErrorLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditErrorLightCountNotBetween(Integer value1, Integer value2) {
            addCriterion("audit_error_light_count not between", value1, value2, "auditErrorLightCount");
            return (Criteria) this;
        }

        public Criteria andAuditTimeIsNull() {
            addCriterion("audit_time is null");
            return (Criteria) this;
        }

        public Criteria andAuditTimeIsNotNull() {
            addCriterion("audit_time is not null");
            return (Criteria) this;
        }

        public Criteria andAuditTimeEqualTo(Date value) {
            addCriterion("audit_time =", value, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeNotEqualTo(Date value) {
            addCriterion("audit_time <>", value, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeGreaterThan(Date value) {
            addCriterion("audit_time >", value, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("audit_time >=", value, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeLessThan(Date value) {
            addCriterion("audit_time <", value, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeLessThanOrEqualTo(Date value) {
            addCriterion("audit_time <=", value, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeIn(List<Date> values) {
            addCriterion("audit_time in", values, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeNotIn(List<Date> values) {
            addCriterion("audit_time not in", values, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeBetween(Date value1, Date value2) {
            addCriterion("audit_time between", value1, value2, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeNotBetween(Date value1, Date value2) {
            addCriterion("audit_time not between", value1, value2, "auditTime");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated do_not_delete_during_merge Wed Oct 12 09:14:04 CST 2022
     */
    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}