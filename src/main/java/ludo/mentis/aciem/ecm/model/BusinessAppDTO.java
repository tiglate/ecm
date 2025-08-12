package ludo.mentis.aciem.ecm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ludo.mentis.aciem.ecm.validation.BusinessAppNameUnique;

import java.time.LocalDateTime;


public class BusinessAppDTO {

    private Long id;

    @NotBlank
    @Size(max = 255)
    @BusinessAppNameUnique
    private String name;

    @Size(max = 45)
    private String code;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    public BusinessAppDTO() {
    	
    }
    
    public BusinessAppDTO(Long id, String code, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
        this.code = code;
		this.name = name;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
        return id;
    }

    public void setId(final Long value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(final String value) {
        this.name = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        this.code = value;
    }

    public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime value) {
		this.createdAt = value;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime value) {
		this.updatedAt = value;
	}
}
