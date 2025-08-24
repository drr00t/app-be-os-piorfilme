package com.github.drr00t.gra.control;

import com.github.drr00t.gra.boundary.entity.AwardNominee;

import java.util.List;

public interface AwardNomineesLoaderRepository {

    void loadAwardNominees(List<AwardNominee> awardNominees);
}
