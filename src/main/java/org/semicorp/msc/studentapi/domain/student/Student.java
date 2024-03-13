package org.semicorp.msc.studentapi.domain.student;


import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Student implements Serializable {

    private String id;
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String sex;

}
