package com.xz.report.schools;

import com.xz.bean.Range;
import com.xz.report.classes.ReportGeneratorInfo;
import com.xz.report.total.TotalBasicScoreReport;
import org.springframework.stereotype.Component;

/**
 * (description)
 * created at 16/05/31
 *
 * @author yiding_he
 */
@ReportGeneratorInfo(range = Range.SCHOOL)
@Component
public class SchoolBasicScoreReport extends TotalBasicScoreReport {

}
