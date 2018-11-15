//package com.yd.ydsp.dal.mybatis;
//
//import com.alibaba.fastjson.JSON;
//import org.apache.ibatis.type.BaseTypeHandler;
//import org.apache.ibatis.type.JdbcType;
//
//import java.sql.CallableStatement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
///**
// * Created by zengyixun on 17/5/20.
// */
//public class JsonTypeHandler extends BaseTypeHandler<Object> {
//
//
//    private String toJson(Object object) {
//        try {
//            return JSON.toJSONString(object);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private Object toObject(String content) {
//        if (content != null && !content.isEmpty()) {
//            try {
//                return JSON.parseObject(content);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            return null;
//        }
//    }
//
//
//    @Override
//    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
//        ps.setString(i, this.toJson(parameter));
//    }
//
//    @Override
//    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
//        return this.toObject(rs.getString(columnName));
//    }
//
//    @Override
//    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//        return this.toObject(rs.getString(columnIndex));
//    }
//
//    @Override
//    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//        return this.toObject(cs.getString(columnIndex));
//    }
//}
