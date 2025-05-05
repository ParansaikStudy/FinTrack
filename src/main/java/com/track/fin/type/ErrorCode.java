package com.track.fin.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST("잘못된 요청입니다."),
    USER_NOT_FOUND("사용자가 없습니다."),
    ACCOUNT_NOT_FOUND("계좌가 없습니다."),
    USER_ACCOUNT_UNMATCH("사용자와 계좌의 소유주가 다릅니다."),
    TRANSACTION_NOT_FOUND("해당 거래가 없습니다."),
    TRANSACTION_ACCOUNT_UN_MATCH("이 거래는 해당 계좌에서 발생한 거래가 아닙니다."),
    CANCEL_MUST_FULLY("부분 취소는 허용되지 않습니다."),
    TOO_OLD_ORDER_TO_CALCEL("1년이 지난 거래는 취소가 불가능하니다."),
    AMOUNT_EXCEED_BALANCE("거래금액이 계좌 잔액보다 큽니다."),
    ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 해지되었습니다."),
    BALANCE_NOT_EMPTY("잔액이 있는 계좌는 해지할 수 없습니다."),
    MAX_ACCOUNT_PER_USER_10("사용자 최대 계좌는 10개입니다."),
    INSUFFICIENT_INITIAL_BALANCE("최소 예치금보다 적은 금액으로 계좌를 개설할 수 없습니다."),
    LOAN_NOT_FOUND("대출 내역을 찾을 수 없습니다.");

    private final String description;
}
