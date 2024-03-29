package com.pxccn.PxcDali2.server.database.mapper;

import com.pxccn.PxcDali2.server.database.model.LightsV2;
import com.pxccn.PxcDali2.server.database.model.LightsV2Example;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface LightsV2Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lights_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @SelectProvider(type=LightsV2SqlProvider.class, method="countByExample")
    long countByExample(LightsV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lights_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @DeleteProvider(type=LightsV2SqlProvider.class, method="deleteByExample")
    int deleteByExample(LightsV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lights_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @Delete({
        "delete from lights_V2",
        "where lightUUID = #{lightuuid,jdbcType=CHAR}"
    })
    int deleteByPrimaryKey(String lightuuid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lights_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @Insert({
        "insert into lights_V2 (lightUUID, syncFlag, ",
        "name, description, ",
        "cabinet_ID, busIndex, ",
        "busType, shortAddress, ",
        "axis_x, axis_y, axis_z)",
        "values (#{lightuuid,jdbcType=CHAR}, #{syncflag,jdbcType=BIT}, ",
        "#{name,jdbcType=NVARCHAR}, #{description,jdbcType=NVARCHAR}, ",
        "#{cabinetId,jdbcType=INTEGER}, #{busindex,jdbcType=INTEGER}, ",
        "#{bustype,jdbcType=VARCHAR}, #{shortaddress,jdbcType=INTEGER}, ",
        "#{axisX,jdbcType=INTEGER}, #{axisY,jdbcType=INTEGER}, #{axisZ,jdbcType=INTEGER})"
    })
    int insert(LightsV2 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lights_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @InsertProvider(type=LightsV2SqlProvider.class, method="insertSelective")
    int insertSelective(LightsV2 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lights_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @SelectProvider(type=LightsV2SqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="lightUUID", property="lightuuid", jdbcType=JdbcType.CHAR, id=true),
        @Result(column="syncFlag", property="syncflag", jdbcType=JdbcType.BIT),
        @Result(column="name", property="name", jdbcType=JdbcType.NVARCHAR),
        @Result(column="description", property="description", jdbcType=JdbcType.NVARCHAR),
        @Result(column="cabinet_ID", property="cabinetId", jdbcType=JdbcType.INTEGER),
        @Result(column="busIndex", property="busindex", jdbcType=JdbcType.INTEGER),
        @Result(column="busType", property="bustype", jdbcType=JdbcType.VARCHAR),
        @Result(column="shortAddress", property="shortaddress", jdbcType=JdbcType.INTEGER),
        @Result(column="axis_x", property="axisX", jdbcType=JdbcType.INTEGER),
        @Result(column="axis_y", property="axisY", jdbcType=JdbcType.INTEGER),
        @Result(column="axis_z", property="axisZ", jdbcType=JdbcType.INTEGER)
    })
    List<LightsV2> selectByExample(LightsV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lights_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @Select({
        "select",
        "lightUUID, syncFlag, name, description, cabinet_ID, busIndex, busType, shortAddress, ",
        "axis_x, axis_y, axis_z",
        "from lights_V2",
        "where lightUUID = #{lightuuid,jdbcType=CHAR}"
    })
    @Results({
        @Result(column="lightUUID", property="lightuuid", jdbcType=JdbcType.CHAR, id=true),
        @Result(column="syncFlag", property="syncflag", jdbcType=JdbcType.BIT),
        @Result(column="name", property="name", jdbcType=JdbcType.NVARCHAR),
        @Result(column="description", property="description", jdbcType=JdbcType.NVARCHAR),
        @Result(column="cabinet_ID", property="cabinetId", jdbcType=JdbcType.INTEGER),
        @Result(column="busIndex", property="busindex", jdbcType=JdbcType.INTEGER),
        @Result(column="busType", property="bustype", jdbcType=JdbcType.VARCHAR),
        @Result(column="shortAddress", property="shortaddress", jdbcType=JdbcType.INTEGER),
        @Result(column="axis_x", property="axisX", jdbcType=JdbcType.INTEGER),
        @Result(column="axis_y", property="axisY", jdbcType=JdbcType.INTEGER),
        @Result(column="axis_z", property="axisZ", jdbcType=JdbcType.INTEGER)
    })
    LightsV2 selectByPrimaryKey(String lightuuid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lights_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @UpdateProvider(type=LightsV2SqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("row") LightsV2 row, @Param("example") LightsV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lights_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @UpdateProvider(type=LightsV2SqlProvider.class, method="updateByExample")
    int updateByExample(@Param("row") LightsV2 row, @Param("example") LightsV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lights_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @UpdateProvider(type=LightsV2SqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(LightsV2 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lights_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @Update({
        "update lights_V2",
        "set syncFlag = #{syncflag,jdbcType=BIT},",
          "name = #{name,jdbcType=NVARCHAR},",
          "description = #{description,jdbcType=NVARCHAR},",
          "cabinet_ID = #{cabinetId,jdbcType=INTEGER},",
          "busIndex = #{busindex,jdbcType=INTEGER},",
          "busType = #{bustype,jdbcType=VARCHAR},",
          "shortAddress = #{shortaddress,jdbcType=INTEGER},",
          "axis_x = #{axisX,jdbcType=INTEGER},",
          "axis_y = #{axisY,jdbcType=INTEGER},",
          "axis_z = #{axisZ,jdbcType=INTEGER}",
        "where lightUUID = #{lightuuid,jdbcType=CHAR}"
    })
    int updateByPrimaryKey(LightsV2 row);
}