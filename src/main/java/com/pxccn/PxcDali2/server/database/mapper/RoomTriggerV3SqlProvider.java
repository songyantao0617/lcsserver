package com.pxccn.PxcDali2.server.database.mapper;

import com.pxccn.PxcDali2.server.database.model.RoomTriggerV3;
import com.pxccn.PxcDali2.server.database.model.RoomTriggerV3Example.Criteria;
import com.pxccn.PxcDali2.server.database.model.RoomTriggerV3Example.Criterion;
import com.pxccn.PxcDali2.server.database.model.RoomTriggerV3Example;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.SQL;

public class RoomTriggerV3SqlProvider {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_trigger_V3
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public String countByExample(RoomTriggerV3Example example) {
        SQL sql = new SQL();
        sql.SELECT("count(*)").FROM("room_trigger_V3");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_trigger_V3
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public String deleteByExample(RoomTriggerV3Example example) {
        SQL sql = new SQL();
        sql.DELETE_FROM("room_trigger_V3");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_trigger_V3
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public String insertSelective(RoomTriggerV3 row) {
        SQL sql = new SQL();
        sql.INSERT_INTO("room_trigger_V3");
        
        if (row.getTriggeruuid() != null) {
            sql.VALUES("triggerUUID", "#{triggeruuid,jdbcType=CHAR}");
        }
        
        if (row.getTriggertype() != null) {
            sql.VALUES("triggerType", "#{triggertype,jdbcType=VARCHAR}");
        }
        
        if (row.getRoomuuid() != null) {
            sql.VALUES("roomUUID", "#{roomuuid,jdbcType=CHAR}");
        }
        
        if (row.getCabinetId() != null) {
            sql.VALUES("cabinet_ID", "#{cabinetId,jdbcType=INTEGER}");
        }
        
        if (row.getTriggerconfigure() != null) {
            sql.VALUES("triggerConfigure", "#{triggerconfigure,jdbcType=NVARCHAR}");
        }
        
        if (row.getFb() != null) {
            sql.VALUES("fb", "#{fb,jdbcType=VARCHAR}");
        }
        
        if (row.getFb2() != null) {
            sql.VALUES("fb2", "#{fb2,jdbcType=NVARCHAR}");
        }
        
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_trigger_V3
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public String selectByExample(RoomTriggerV3Example example) {
        SQL sql = new SQL();
        if (example != null && example.isDistinct()) {
            sql.SELECT_DISTINCT("triggerUUID");
        } else {
            sql.SELECT("triggerUUID");
        }
        sql.SELECT("triggerType");
        sql.SELECT("roomUUID");
        sql.SELECT("cabinet_ID");
        sql.SELECT("triggerConfigure");
        sql.SELECT("fb");
        sql.SELECT("fb2");
        sql.FROM("room_trigger_V3");
        applyWhere(sql, example, false);
        
        if (example != null && example.getOrderByClause() != null) {
            sql.ORDER_BY(example.getOrderByClause());
        }
        
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_trigger_V3
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public String updateByExampleSelective(Map<String, Object> parameter) {
        RoomTriggerV3 row = (RoomTriggerV3) parameter.get("row");
        RoomTriggerV3Example example = (RoomTriggerV3Example) parameter.get("example");
        
        SQL sql = new SQL();
        sql.UPDATE("room_trigger_V3");
        
        if (row.getTriggeruuid() != null) {
            sql.SET("triggerUUID = #{row.triggeruuid,jdbcType=CHAR}");
        }
        
        if (row.getTriggertype() != null) {
            sql.SET("triggerType = #{row.triggertype,jdbcType=VARCHAR}");
        }
        
        if (row.getRoomuuid() != null) {
            sql.SET("roomUUID = #{row.roomuuid,jdbcType=CHAR}");
        }
        
        if (row.getCabinetId() != null) {
            sql.SET("cabinet_ID = #{row.cabinetId,jdbcType=INTEGER}");
        }
        
        if (row.getTriggerconfigure() != null) {
            sql.SET("triggerConfigure = #{row.triggerconfigure,jdbcType=NVARCHAR}");
        }
        
        if (row.getFb() != null) {
            sql.SET("fb = #{row.fb,jdbcType=VARCHAR}");
        }
        
        if (row.getFb2() != null) {
            sql.SET("fb2 = #{row.fb2,jdbcType=NVARCHAR}");
        }
        
        applyWhere(sql, example, true);
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_trigger_V3
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public String updateByExample(Map<String, Object> parameter) {
        SQL sql = new SQL();
        sql.UPDATE("room_trigger_V3");
        
        sql.SET("triggerUUID = #{row.triggeruuid,jdbcType=CHAR}");
        sql.SET("triggerType = #{row.triggertype,jdbcType=VARCHAR}");
        sql.SET("roomUUID = #{row.roomuuid,jdbcType=CHAR}");
        sql.SET("cabinet_ID = #{row.cabinetId,jdbcType=INTEGER}");
        sql.SET("triggerConfigure = #{row.triggerconfigure,jdbcType=NVARCHAR}");
        sql.SET("fb = #{row.fb,jdbcType=VARCHAR}");
        sql.SET("fb2 = #{row.fb2,jdbcType=NVARCHAR}");
        
        RoomTriggerV3Example example = (RoomTriggerV3Example) parameter.get("example");
        applyWhere(sql, example, true);
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_trigger_V3
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    public String updateByPrimaryKeySelective(RoomTriggerV3 row) {
        SQL sql = new SQL();
        sql.UPDATE("room_trigger_V3");
        
        if (row.getTriggertype() != null) {
            sql.SET("triggerType = #{triggertype,jdbcType=VARCHAR}");
        }
        
        if (row.getRoomuuid() != null) {
            sql.SET("roomUUID = #{roomuuid,jdbcType=CHAR}");
        }
        
        if (row.getCabinetId() != null) {
            sql.SET("cabinet_ID = #{cabinetId,jdbcType=INTEGER}");
        }
        
        if (row.getTriggerconfigure() != null) {
            sql.SET("triggerConfigure = #{triggerconfigure,jdbcType=NVARCHAR}");
        }
        
        if (row.getFb() != null) {
            sql.SET("fb = #{fb,jdbcType=VARCHAR}");
        }
        
        if (row.getFb2() != null) {
            sql.SET("fb2 = #{fb2,jdbcType=NVARCHAR}");
        }
        
        sql.WHERE("triggerUUID = #{triggeruuid,jdbcType=CHAR}");
        
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_trigger_V3
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    protected void applyWhere(SQL sql, RoomTriggerV3Example example, boolean includeExamplePhrase) {
        if (example == null) {
            return;
        }
        
        String parmPhrase1;
        String parmPhrase1_th;
        String parmPhrase2;
        String parmPhrase2_th;
        String parmPhrase3;
        String parmPhrase3_th;
        if (includeExamplePhrase) {
            parmPhrase1 = "%s #{example.oredCriteria[%d].allCriteria[%d].value}";
            parmPhrase1_th = "%s #{example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s}";
            parmPhrase2 = "%s #{example.oredCriteria[%d].allCriteria[%d].value} and #{example.oredCriteria[%d].criteria[%d].secondValue}";
            parmPhrase2_th = "%s #{example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s} and #{example.oredCriteria[%d].criteria[%d].secondValue,typeHandler=%s}";
            parmPhrase3 = "#{example.oredCriteria[%d].allCriteria[%d].value[%d]}";
            parmPhrase3_th = "#{example.oredCriteria[%d].allCriteria[%d].value[%d],typeHandler=%s}";
        } else {
            parmPhrase1 = "%s #{oredCriteria[%d].allCriteria[%d].value}";
            parmPhrase1_th = "%s #{oredCriteria[%d].allCriteria[%d].value,typeHandler=%s}";
            parmPhrase2 = "%s #{oredCriteria[%d].allCriteria[%d].value} and #{oredCriteria[%d].criteria[%d].secondValue}";
            parmPhrase2_th = "%s #{oredCriteria[%d].allCriteria[%d].value,typeHandler=%s} and #{oredCriteria[%d].criteria[%d].secondValue,typeHandler=%s}";
            parmPhrase3 = "#{oredCriteria[%d].allCriteria[%d].value[%d]}";
            parmPhrase3_th = "#{oredCriteria[%d].allCriteria[%d].value[%d],typeHandler=%s}";
        }
        
        StringBuilder sb = new StringBuilder();
        List<Criteria> oredCriteria = example.getOredCriteria();
        boolean firstCriteria = true;
        for (int i = 0; i < oredCriteria.size(); i++) {
            Criteria criteria = oredCriteria.get(i);
            if (criteria.isValid()) {
                if (firstCriteria) {
                    firstCriteria = false;
                } else {
                    sb.append(" or ");
                }
                
                sb.append('(');
                List<Criterion> criterions = criteria.getAllCriteria();
                boolean firstCriterion = true;
                for (int j = 0; j < criterions.size(); j++) {
                    Criterion criterion = criterions.get(j);
                    if (firstCriterion) {
                        firstCriterion = false;
                    } else {
                        sb.append(" and ");
                    }
                    
                    if (criterion.isNoValue()) {
                        sb.append(criterion.getCondition());
                    } else if (criterion.isSingleValue()) {
                        if (criterion.getTypeHandler() == null) {
                            sb.append(String.format(parmPhrase1, criterion.getCondition(), i, j));
                        } else {
                            sb.append(String.format(parmPhrase1_th, criterion.getCondition(), i, j,criterion.getTypeHandler()));
                        }
                    } else if (criterion.isBetweenValue()) {
                        if (criterion.getTypeHandler() == null) {
                            sb.append(String.format(parmPhrase2, criterion.getCondition(), i, j, i, j));
                        } else {
                            sb.append(String.format(parmPhrase2_th, criterion.getCondition(), i, j, criterion.getTypeHandler(), i, j, criterion.getTypeHandler()));
                        }
                    } else if (criterion.isListValue()) {
                        sb.append(criterion.getCondition());
                        sb.append(" (");
                        List<?> listItems = (List<?>) criterion.getValue();
                        boolean comma = false;
                        for (int k = 0; k < listItems.size(); k++) {
                            if (comma) {
                                sb.append(", ");
                            } else {
                                comma = true;
                            }
                            if (criterion.getTypeHandler() == null) {
                                sb.append(String.format(parmPhrase3, i, j, k));
                            } else {
                                sb.append(String.format(parmPhrase3_th, i, j, k, criterion.getTypeHandler()));
                            }
                        }
                        sb.append(')');
                    }
                }
                sb.append(')');
            }
        }
        
        if (sb.length() > 0) {
            sql.WHERE(sb.toString());
        }
    }
}