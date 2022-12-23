package project.hrpjt.appointment.entity.enumeration;

public enum ApprovementStatus {
    LEADER_PENDING_APPR, // 조직장 승인 대기 (default)
    CEO_PENDING_APPR, // CEO 승인 대기
    APPR, // 최종승인
    REJECTED // 반려
}
