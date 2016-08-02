package com.westernalliancebancorp.positivepay.model;

/**
 * Class representing Exception types
 * @author Moumita Ghosh
 */

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

@EntityListeners(AuditListener.class)
@Entity
@Table(name = "EXCEPTION_TYPE")
public class ExceptionType implements Serializable, Auditable {
	private static final long serialVersionUID = 1L;

	public enum EXCEPTION_TYPE {
		PresentedNotIssuedException {
			public String toString() {
				return "Presented Not Issued Exception";
			}
		},
		InvalidAccountException {
			public String toString() {
				return "Invalid Account Exception";
			}
		},
		DuplicateStopException {
			public String toString() {
				return "Duplicate Stop Exception";
			}
		},
        DuplicateStopItemException {
            public String toString() {
                return "Duplicate Stop Item Exception";
            }
        },
		DuplicateIssuedException {
			public String toString() {
				return "Duplicate Issued Exception";
			}
		},
		InvalidStopAmountException {
			public String toString() {
				return "Invalid Stop Amount Exception";
			}
		},
		StopNotIssuedException {
			public String toString() {
				return "Stop Not Issued Exception";
			}
		},
		DuplicatePresentedItemException {
			public String toString() {
				return "Duplicate Presented Item Exception";
			}
		},
		PaidNotIssuedException {
			public String toString() {
				return "Paid Not Issued Exception";
			}
		},
		DuplicatePaidItemException {
			public String toString() {
				return "Duplicate Paid Item Exception";
			}
		},
		DuplicatePaidException {
			public String toString() {
				return "Duplicate Paid Exception";
			}
		},
		StopPaidException {
			public String toString() {
				return "Stop Paid Exception";
			}
		},
		DuplicateIssuedItemException {
			public String toString() {
				return "Duplicate Issued Item Exception";
			}
		},
		InvalidAmountException {
			public String toString() {
				return "Invalid Amount Exception";
			}
		},
		PayeeMatchException {
			public String toString() {
				return "Payee Match Exception";
			}
		},
		StopPresentedException {
			public String toString() {
				return "Stop PresentedException";
			}
		},
		VoidNotIssuedException {
			public String toString() {
				return "Void Not Issued Exception";
			}
		},
		SequenceException_VoidAfterEscheated {
			public String toString() {
				return "Sequence Exception Void After Escheated";
			}
		},
		SequenceException_StopAfterEscheated {
			public String toString() {
				return "Sequence Exception Stop After Escheated";
			}
		},
		SequenceException_StopAfterPaid {
			public String toString() {
				return "Sequence Exception Stop After Paid";
			}
		},
		SequenceException_IssuedAfterStop {
			public String toString() {
				return "Sequence Exception Issued After Stop";
			}
		},
		SequenceException_IssuedAfterVoid {
			public String toString() {
				return "Sequence Exception Issued After Void";
			}
		},
		SequenceException_PaidAfterEscheated {
			public String toString() {
				return "Sequence Exception Paid After Escheated";
			}
		},
		DuplicateVoidException {
			public String toString() {
				return "Duplicate Void Exception";
			}
		},
		StalePaidException {
			public String toString() {
				return "Stale Paid Exception";
			}
		},
		SequenceException_VoidAfterPaid {
			public String toString() {
				return "Sequence Exception Void After Paid";
			}
		},
		IssuedAmtExceededException {
			public String toString() {
				return "Issued Amount Exceeded Exception";
			}
		},
		PaidAmtExceededException {
			public String toString() {
				return "Paid Amount Exceeded Exception";
			}
		},
		SequenceException_PaidBeforeIssuedDate {
			public String toString() {
				return "Sequence Exception Paid Before Issued Date";
			}
		},
		VoidPaidException {
			public String toString() {
				return "Void Paid Exception";
			}
		},
		NotPaidItemException {
			public String toString() {
				return "Not Paid Item Exception";
			}
		},
		SequenceException_StopAfterVoid {
			public String toString() {
				return "Sequence Exception Stop After Void";
			}
		},
		SequenceException_VoidAfterStop {
			public String toString() {
				return "Sequence Exception Void After Stop";
			}
		},
		DUPLICATE_CHECK_IN_DATABASE {
			public String toString() {
				return "Duplicate check in database";
			}
		},
        DUPLICATE_DATA_IN_DATABASE {
            public String toString() {
                return "Duplicate data in database";
            }
        },
		DUPLICATE_CHECK_IN_FILE {
			public String toString() {
				return "Duplicate check in file";
			}
		},
        DUPLICATE_PAID_ITEM_IN_FILE {
            public String toString() {
                return "Duplicate paid item in file";
            }
        },
        DUPLICATE_STOP_ITEM_IN_FILE {
            public String toString() {
                return "Duplicate stop item in file";
            }
        },
        DUPLICATE_STOP_PRESENTED_ITEM_IN_FILE {
            public String toString() {
                return "Duplicate stop item in file";
            }
        },
        PAID_DATA_IN_WRONG_FORMAT {
            public String toString() {
                return "Paid data in wrong format";
            }
        },
        STOP_DATA_IN_WRONG_FORMAT {
            public String toString() {
                return "Stop data in wrong format";
            }
        },
        STOP_PRESENTED_DATA_IN_WRONG_FORMAT {
            public String toString() {
                return "Stop presented data in wrong format";
            }
        },
		DUPLICATE_CHECK_IN_FILE_AMOUNT_VARIED {
			public String toString() {
				return "Duplicate check in file, amount varied";
			}
		},
		ACCOUNT_NOT_FOUND {
			public String toString() {
				return "Account not found";
			}
		},
        PAID_ACCOUNT_NOT_FOUND {
            public String toString() {
                return "Account not found";
            }
        },
        STOP_ACCOUNT_NOT_FOUND {
            public String toString() {
                return "Stop account not found";
            }
        },
        STOP_PRESENTED_ACCOUNT_NOT_FOUND {
            public String toString() {
                return "Stop presented account not found";
            }
        },
        WRONG_ITEM_TYPE {
            public String toString() {
                return "Wrong item type";
            }
        },
		CHECK_IN_WRONG_ITEM_CODE {
			public String toString() {
				return "Check in wrong item code";
			}
		},
		PAID_OR_STOP_CHECK_NOT_ALLOWED {
			public String toString() {
				return "Check with item code P and S are not allowed.";
			}
		},
		CHECK_IN_WRONG_DATA_FORMAT {
			public String toString() {
				return "Check in wrong data format";
			}
		},
        ZERO_NUMBERED_CHECK {
            public String toString() {
                return "Zero numbered check";
            }
        }
	}

	@Column(name = "ID")
	@Id
	@GeneratedValue
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "NAME", length = 50, nullable = false)
	private EXCEPTION_TYPE exceptionType;

	@Column(name = "DESCRIPTION", length = 255, nullable = false)
	private String description;

    @Column(name = "LABEL", length = 75, nullable = false)
    private String label;

	@Column(name = "IS_ACTIVE", nullable = false)
	private boolean isActive = true;
	
	@JsonIgnore
	@OneToMany(mappedBy = "exceptionType", targetEntity = Check.class)
    private Set<Check> checks = new HashSet<Check>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "exceptionType", targetEntity = CheckHistory.class)
    private Set<CheckHistory> checkHistorySet = new HashSet<CheckHistory>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EXCEPTION_TYPE getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(EXCEPTION_TYPE exceptionType) {
		this.exceptionType = exceptionType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public Set<Check> getChecks() {
		return checks;
	}

	public void setChecks(Set<Check> checks) {
		this.checks = checks;
	}

	public Set<CheckHistory> getCheckHistorySet() {
		return checkHistorySet;
	}

	public void setCheckHistorySet(Set<CheckHistory> checkHistorySet) {
		this.checkHistorySet = checkHistorySet;
	}

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Embedded
	private AuditInfo auditInfo = new AuditInfo();

	public AuditInfo getAuditInfo() {
		return auditInfo;
	}

	public void setAuditInfo(AuditInfo auditInfo) {
		this.auditInfo = auditInfo;
	}
}
