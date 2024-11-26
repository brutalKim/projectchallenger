package site.challenger.project_challenger.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReportRequestDto {
	private String reportKind;
	private long targetNo;
	private long targetUserNo;
	private String targetKind;
	private String reportContent;

}
