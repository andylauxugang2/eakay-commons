package cn.eakay.commons.dao.persistence.mybatis.handlers.joda;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.sql.*;

/**
 * 采用实现 TypeHandler 方式实现joda LocalTime 与库类型time装换
 *
 * 支持本地时间
 *
 * @author xugang
 */
@MappedTypes(LocalTime.class)
public class LocalTimeTypeHandler implements TypeHandler {

    /**
     * 用于定义在Mybatis设置参数时该如何把Java类型的参数转换为对应的数据库类型
     *
     * @param ps        当前的PreparedStatement对象
     * @param i         当前参数的位置
     * @param parameter 当前参数的Java对象
     * @param jdbcType  当前参数的数据库类型
     * @throws SQLException
     * @see org.apache.ibatis.type.TypeHandler#setParameter(java.sql.PreparedStatement, int, java.lang.Object, org.apache.ibatis.type.JdbcType)
     */
    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        LocalTime time = (LocalTime) parameter;
        if (time != null) {
            DateTime datetime = new DateTime(1970, 1, 1, time.getHourOfDay(), time.getMinuteOfHour(),
                    time.getSecondOfMinute(), 0);
            ps.setTime(i, new Time(datetime.toDate().getTime()));
        } else {
            ps.setTime(i, null);
        }
    }

    /**
     * 用于在Mybatis获取 数据结果集 时如何把数据库类型转换为对应的Java类型
     *
     * @param rs         当前的结果集
     * @param columnName 当前的字段名称
     * @return 转换后的Java对象
     * @throws SQLException
     * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.ResultSet, java.lang.String)
     */
    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        Time time = rs.getTime(columnName);
        if (time != null) {
            return new LocalTime(time.getTime());
        } else {
            return null;
        }

    }

    /**
     * 用于在Mybatis通过 字段位置 获取字段数据时把数据库类型转换为对应的Java类型
     *
     * @param rs          当前的结果集
     * @param columnIndex 当前字段的位置
     * @return 转换后的Java对象
     * @throws SQLException
     */
    @Override
    public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
        Time time = rs.getTime(columnIndex);
        if (time != null) {
            return new LocalTime(time.getTime());
        } else {
            return null;
        }
    }

    /**
     * 用于Mybatis在调用 存储过程 后把数据库类型的数据转换为对应的Java类型
     *
     * @param cs          当前的CallableStatement执行后的CallableStatement
     * @param columnIndex 当前输出参数的位置
     * @return
     * @throws SQLException
     * @see org.apache.ibatis.type.TypeHandler#getResult(java.sql.CallableStatement, int)
     */
    public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
        Time time = cs.getTime(columnIndex);
        if (time != null) {
            return new LocalTime(time.getTime());
        } else {
            return null;
        }
    }

}
