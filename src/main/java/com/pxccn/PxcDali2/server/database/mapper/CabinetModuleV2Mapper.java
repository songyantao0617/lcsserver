package com.pxccn.PxcDali2.server.database.mapper;

import com.pxccn.PxcDali2.server.database.model.CabinetModuleV2;
import com.pxccn.PxcDali2.server.database.model.CabinetModuleV2Example;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CabinetModuleV2Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    long countByExample(CabinetModuleV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int deleteByExample(CabinetModuleV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int deleteByPrimaryKey(@Param("cabinetId") Integer cabinetId, @Param("terminalIndex") Integer terminalIndex);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int insert(CabinetModuleV2 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int insertSelective(CabinetModuleV2 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    List<CabinetModuleV2> selectByExample(CabinetModuleV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    CabinetModuleV2 selectByPrimaryKey(@Param("cabinetId") Integer cabinetId, @Param("terminalIndex") Integer terminalIndex);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int updateByExampleSelective(@Param("row") CabinetModuleV2 row, @Param("example") CabinetModuleV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int updateByExample(@Param("row") CabinetModuleV2 row, @Param("example") CabinetModuleV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int updateByPrimaryKeySelective(CabinetModuleV2 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_module_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int updateByPrimaryKey(CabinetModuleV2 row);
}