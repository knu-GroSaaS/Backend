package com.grolabs.caselist.dto.History;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteHistoryDto {
    public String requestername;
    public String username;
    public Timestamp DeletedAt;
}
