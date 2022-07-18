package com.pxccn.PxcDali2.server.database.mapper;

import com.pxccn.PxcDali2.server.database.model.RoomUnitV3;
import com.pxccn.PxcDali2.server.database.model.RoomUnitV3Example;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RoomUnitV3Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_unit_V3
     *
     * @mbg.generated Mon Jul 18 10:18:42 CST 2022
     */
    long countByExample(RoomUnitV3Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_unit_V3
     *
     * @mbg.generated Mon Jul 18 10:18:42 CST 2022
     */
    int deleteByExample(RoomUnitV3Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_unit_V3
     *
     * @mbg.generated Mon Jul 18 10:18:42 CST 2022
     */
    int deleteByPrimaryKey(String roomuuid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_unit_V3
     *
     * @mbg.generated Mon Jul 18 10:18:42 CST 2022
     */
    int insert(RoomUnitV3 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_unit_V3
     *
     * @mbg.generated Mon Jul 18 10:18:42 CST 2022
     */
    int insertSelective(RoomUnitV3 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_unit_V3
     *
     * @mbg.generated Mon Jul 18 10:18:42 CST 2022
     */
    List<RoomUnitV3> selectByExample(RoomUnitV3Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_unit_V3
     *
     * @mbg.generated Mon Jul 18 10:18:42 CST 2022
     */
    RoomUnitV3 selectByPrimaryKey(String roomuuid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_unit_V3
     *
     * @mbg.generated Mon Jul 18 10:18:42 CST 2022
     */
    int updateByExampleSelective(@Param("row") RoomUnitV3 row, @Param("example") RoomUnitV3Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_unit_V3
     *
     * @mbg.generated Mon Jul 18 10:18:42 CST 2022
     */
    int updateByExample(@Param("row") RoomUnitV3 row, @Param("example") RoomUnitV3Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_unit_V3
     *
     * @mbg.generated Mon Jul 18 10:18:42 CST 2022
     */
    int updateByPrimaryKeySelective(RoomUnitV3 row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_unit_V3
     *
     * @mbg.generated Mon Jul 18 10:18:42 CST 2022
     */
    int updateByPrimaryKey(RoomUnitV3 row);
}