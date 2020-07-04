package org.seasar.mayaa.regressions.issue13;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import org.seasar.mayaa.cycle.ServiceCycle;
import org.seasar.mayaa.engine.Page;
import org.seasar.mayaa.engine.processor.ProcessStatus;
import org.seasar.mayaa.engine.processor.ProcessorProperty;
import org.seasar.mayaa.impl.cycle.CycleUtil;
import org.seasar.mayaa.impl.engine.processor.TemplateProcessorSupport;
import org.seasar.mayaa.impl.util.DateFormatPool;

/**
 * {@link java.util.Date}を指定フォーマットで文字列に変換して出力するプロセッサ。
 * 内部的には{@link SimpleDateFormat}。
 *
 */
public class FormatDateProcessor extends TemplateProcessorSupport {

    private static final long serialVersionUID = -2331626109260967664L;

    private LocalDateTime value;
    private ProcessorProperty _default;
    private String _pattern;

    public void initialize() {
        if (_pattern == null) {
            _pattern = new SimpleDateFormat().toPattern();
        }
    }

    // MLD property, expectedClass=java.lang.String
    // public void setValue(ProcessorProperty value) {
    //     _value = value;
    // }

    public void setValue(LocalDateTime value) {
        this.value = value;
    }

    public LocalDateTime getValue() {
        return value;
    }

    public void setDefault(ProcessorProperty defaultValue) {
        _default = defaultValue;
    }

    public void setPattern(String pattern) {
        _pattern = pattern;
    }

    public ProcessStatus doStartProcess(Page topLevelPage) {
        if (value != null) {
            DateFormat formatter = DateFormatPool.borrowFormat(_pattern);
            ServiceCycle cycle = CycleUtil.getServiceCycle();
            cycle.getResponse().write(formatter.format(value));
            DateFormatPool.returnFormat(formatter);
        }
        return ProcessStatus.SKIP_BODY;
    }
}