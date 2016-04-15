package cn.eakay.commons.dao.persistence.mybatis.handlers.joda;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeReference;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.*;

/**
 * 采用继承 BaseTypeHandler 方式实现jodatime与库类型date装换
 * <p/>
 * BaseTypeHandler:对TypeHandler接口方法的null值的情况做了一个过滤，取值和设值的方法由子类来实现
 * TypeReference:getRawType()方法可以获取到当前TypeHandler所使用泛型的原始类型 Mybatis在注册TypeHandler的时候灵活使用
 *
 * @author xugang
 *
 * @MappedTypes Mybatis在注册DateTimeTypeHandler时 把 MappedTypes 作为 javaType
 * 如果没有 @MappedTypes 则Mybatis在注册DateTimeTypeHandler时 调TypeReference#getRawType()取当前TypeHandler泛型对应的javaType类型完成注册
 *
 * 注册方式1：使用typeHandler元素注册 （另一种是扫包）
 * <typeHandlers>
 *     <typeHandler handler="cn.eakay.commons.dao.persistence.mybatis.handlers.joda.DateTimeTypeHandler" javaType="org.joda.time.DateTime" jdbcType="TIME"/>
 * </typeHandlers>
 *
 * @see BaseTypeHandler
 * @see TypeReference
 */
@MappedTypes({DateTime.class})
public class DateTimeTypeHandler extends BaseTypeHandler<DateTime> {

    private DateTime getDateTime(Timestamp ts) {
        if (ts != null) {
            return new DateTime(ts.getTime(), DateTimeZone.UTC);
        } else {
            return null;
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DateTime parameter, JdbcType jdbcType) throws SQLException {
        //BaseTypeHandler做了parameter判空处理 我们直接拿来用
        ps.setTimestamp(i, new Timestamp(parameter.getMillis()));
    }

    @Override
    public DateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Timestamp ts = rs.getTimestamp(columnName);
        return getDateTime(ts);
    }

    @Override
    public DateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Timestamp ts = rs.getTimestamp(columnIndex);
        return getDateTime(ts);
    }

    @Override
    public DateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Timestamp ts = cs.getTimestamp(columnIndex);
        return getDateTime(ts);
    }

}
