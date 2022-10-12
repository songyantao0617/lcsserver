package com.pxccn.PxcDali2.server.database.mapper;

import com.pxccn.PxcDali2.server.database.model.PointsV2;
import com.pxccn.PxcDali2.server.database.model.PointsV2Example;
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

public interface PointsV2Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table points_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @SelectProvider(type=PointsV2SqlProvider.class, method="countByExample")
    long countByExample(PointsV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table points_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @DeleteProvider(type=PointsV2SqlProvider.class, method="deleteByExample")
    int deleteByExample(PointsV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table points_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @Delete({
        "delete from points_V2",
        "where cabinet_ID = #{cabinetId,jdbcType=INTEGER}",
          "and pointStr = #{pointstr,jdbcType=VARCHAR}"
    })
    int deleteByPrimaryKey(@Param("cabinetId") Integer cabinetId, @Param("pointstr") String pointstr);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table points_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @Insert({
        "insert into points_V2 (cabinet_ID, pointStr, ",
        "type, busIndex, ",
        "busType, name, ",
        "description, sourceTrigger, ",
        "filter, axis_x, axis_y, ",
        "axis_z)",
        "values (#{cabinetId,jdbcType=INTEGER}, #{pointstr,jdbcType=VARCHAR}, ",
        "#{type,jdbcType=VARCHAR}, #{busindex,jdbcType=INTEGER}, ",
        "#{bustype,jdbcType=VARCHAR}, #{name,jdbcType=NVARCHAR}, ",
        "#{description,jdbcType=NVARCHAR}, #{sourcetrigger,jdbcType=BIT}, ",
        "#{filter,jdbcType=VARCHAR}, #{axisX,jdbcType=INTEGER}, #{axisY,jdbcType=INTEGER}, ",
        "#{axisZ,jdbcType=INTEGER})"
    })
    int insert(PointsV2 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table points_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @InsertProvider(type=PointsV2SqlProvider.class, method="insertSelective")
    int insertSelective(PointsV2 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table points_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @SelectProvider(type=PointsV2SqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="cabinet_ID", property="cabinetId", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="pointStr", property="pointstr", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="type", property="type", jdbcType=JdbcType.VARCHAR),
        @Result(column="busIndex", property="busindex", jdbcType=JdbcType.INTEGER),
        @Result(column="busType", property="bustype", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.NVARCHAR),
        @Result(column="description", property="description", jdbcType=JdbcType.NVARCHAR),
        @Result(column="sourceTrigger", property="sourcetrigger", jdbcType=JdbcType.BIT),
        @Result(column="filter", property="filter", jdbcType=JdbcType.VARCHAR),
        @Result(column="axis_x", property="axisX", jdbcType=JdbcType.INTEGER),
        @Result(column="axis_y", property="axisY", jdbcType=JdbcType.INTEGER),
        @Result(column="axis_z", property="axisZ", jdbcType=JdbcType.INTEGER)
    })
    List<PointsV2> selectByExample(PointsV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table points_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @Select({
        "select",
        "cabinet_ID, pointStr, type, busIndex, busType, name, description, sourceTrigger, ",
        "filter, axis_x, axis_y, axis_z",
        "from points_V2",
        "where cabinet_ID = #{cabinetId,jdbcType=INTEGER}",
          "and pointStr = #{pointstr,jdbcType=VARCHAR}"
    })
    @Results({
        @Result(column="cabinet_ID", property="cabinetId", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="pointStr", property="pointstr", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="type", property="type", jdbcType=JdbcType.VARCHAR),
        @Result(column="busIndex", property="busindex", jdbcType=JdbcType.INTEGER),
        @Result(column="busType", property="bustype", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.NVARCHAR),
        @Result(column="description", property="description", jdbcType=JdbcType.NVARCHAR),
        @Result(column="sourceTrigger", property="sourcetrigger", jdbcType=JdbcType.BIT),
        @Result(column="filter", property="filter", jdbcType=JdbcType.VARCHAR),
        @Result(column="axis_x", property="axisX", jdbcType=JdbcType.INTEGER),
        @Result(column="axis_y", property="axisY", jdbcType=JdbcType.INTEGER),
        @Result(column="axis_z", property="axisZ", jdbcType=JdbcType.INTEGER)
    })
    PointsV2 selectByPrimaryKey(@Param("cabinetId") Integer cabinetId, @Param("pointstr") String pointstr);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table points_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @UpdateProvider(type=PointsV2SqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("row") PointsV2 row, @Param("example") PointsV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table points_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @UpdateProvider(type=PointsV2SqlProvider.class, method="updateByExample")
    int updateByExample(@Param("row") PointsV2 row, @Param("example") PointsV2Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table points_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @UpdateProvider(type=PointsV2SqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(PointsV2 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table points_V2
     *
     * @mbg.generated Wed Oct 12 09:14:04 CST 2022
     */
    @Update({
        "update points_V2",
        "set type = #{type,jdbcType=VARCHAR},",
          "busIndex = #{busindex,jdbcType=INTEGER},",
          "busType = #{bustype,jdbcType=VARCHAR},",
          "name = #{name,jdbcType=NVARCHAR},",
          "description = #{description,jdbcType=NVARCHAR},",
          "sourceTrigger = #{sourcetrigger,jdbcType=BIT},",
          "filter = #{filter,jdbcType=VARCHAR},",
          "axis_x = #{axisX,jdbcType=INTEGER},",
          "axis_y = #{axisY,jdbcType=INTEGER},",
          "axis_z = #{axisZ,jdbcType=INTEGER}",
        "where cabinet_ID = #{cabinetId,jdbcType=INTEGER}",
          "and pointStr = #{pointstr,jdbcType=VARCHAR}"
    })
    int updateByPrimaryKey(PointsV2 row);
}