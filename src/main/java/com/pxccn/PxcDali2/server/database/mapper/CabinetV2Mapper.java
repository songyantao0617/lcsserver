package com.pxccn.PxcDali2.server.database.mapper;

import com.pxccn.PxcDali2.server.database.model.CabinetV2;
import com.pxccn.PxcDali2.server.database.model.CabinetV2Example;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CabinetV2Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    long countByExample(CabinetV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int deleteByExample(CabinetV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int deleteByPrimaryKey(Integer cabinetId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int insert(CabinetV2 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int insertSelective(CabinetV2 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    List<CabinetV2> selectByExample(CabinetV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    CabinetV2 selectByPrimaryKey(Integer cabinetId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int updateByExampleSelective(@Param("row") CabinetV2 row, @Param("example") CabinetV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int updateByExample(@Param("row") CabinetV2 row, @Param("example") CabinetV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int updateByPrimaryKeySelective(CabinetV2 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cabinet_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    int updateByPrimaryKey(CabinetV2 row);
}