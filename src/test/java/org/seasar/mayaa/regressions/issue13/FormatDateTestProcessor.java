package org.seasar.mayaa.regressions.issue13;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.seasar.mayaa.cycle.ServiceCycle;
import org.seasar.mayaa.engine.Page;
import org.seasar.mayaa.engine.processor.ProcessStatus;
import org.seasar.mayaa.engine.processor.ProcessorProperty;
import org.seasar.mayaa.impl.cycle.CycleUtil;
import org.seasar.mayaa.impl.engine.processor.TemplateProcessorSupport;

/**
 * {@link java.util.Date}を指定フォーマットで文字列に変換して出力するプロセッサ。
 * 内部的には{@link SimpleDateFormat}。
 *
 */
public class FormatDateTestProcessor extends TemplateProcessorSupport {

    private static final long serialVersionUID = -2331626109260967664L;

    private ProcessorProperty _value;
    private String _pattern;

    public void initialize() {
        if (_pattern == null) {
            _pattern = new SimpleDateFormat().toPattern();
        }
    }

    public void setValue(ProcessorProperty value) {
        this._value = value;
    }

    public ProcessorProperty getValue() {
        return _value;
    }

    public void setPattern(String pattern) {
        _pattern = pattern;
    }

    public ProcessStatus doStartProcess(Page topLevelPage) {
        if (_value != null) {
            ServiceCycle cycle = CycleUtil.getServiceCycle();
            cycle.getResponse().write(format(_value));
        }
        return ProcessStatus.SKIP_BODY;
    }

    private String format(ProcessorProperty property) {
        Object result = property.getValue().execute(null);
        if (result != null) {
            if (result instanceof LocalDateTime) {
                LocalDateTime date = (LocalDateTime) result;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(_pattern);
                String formattedValue = date.format(formatter);
                return formattedValue;
            }

            throw new IllegalArgumentException(
                    "argument type mismatch: " + result.getClass().getName());
        }
        return "";
    }

}