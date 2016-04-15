package cn.eakay.commons.dao.persistence.mybatis.handlers.joda;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.sql.*;

/**
 * 采用实现 TypeHandler 方式实现joda LocalDate 与库类型 date 装换
 * new LocalDate(2016, 4, 7) output 2016-04-07
 *
 * 默认是UTC时间
 *
 * 方法注释 @see LocalTimeTypeHandler
 * @author xugang
 */
@MappedTypes(LocalDate.class)
public class LocalDateTypeHandler implements TypeHandler {

    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        LocalDate date = (LocalDate) parameter;
        if (date != null) {
            ps.setDate(i, new Date(date.toDateTimeAtStartOfDay().toDate().getTime()));
        } else {
            ps.setDate(i, null);
        }
    }

    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        Date date = rs.getDate(columnName);
        if (date != null) {
            return new LocalDate(date.getTime(), DateTimeZone.UTC);
        } else {
            return null;
        }
    }

    @Override
    public Object getResult(ResultSet resultSet, int i) throws SQLException {
        Date date = resultSet.getDate(i);
        if (date != null) {
            return new LocalDate(date.getTime(), DateTimeZone.UTC);
        } else {
            return null;
        }
    }

    @Override
    public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
        Date date = cs.getDate(columnIndex);
        if (date != null) {
            return new LocalDate(date.getTime(), DateTimeZone.UTC);
        } else {
            return null;
        }
    }

}
