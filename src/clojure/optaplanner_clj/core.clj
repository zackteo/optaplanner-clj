(ns optaplanner-clj.core
  (:import [optaplanner_clj Room Lesson Timeslot TimeTable TimeTableConstraintProvider]
           [java.time DayOfWeek LocalTime]
           [java.util UUID Collections]
           [org.optaplanner.core.api.solver SolverManager SolverFactory]
           [org.optaplanner.core.config.solver SolverConfig]
           [org.optaplanner.core.config.solver.termination TerminationConfig]
           [org.optaplanner.core.config.score.director ScoreDirectorFactoryConfig]))

(defn generateProblem []
  (let [timeslotList (list
                       (Timeslot. (DayOfWeek/MONDAY) (LocalTime/of 8 30) (LocalTime/of 9 30))
                       (Timeslot. (DayOfWeek/MONDAY) (LocalTime/of 9 30) (LocalTime/of 10 30))
                       (Timeslot. (DayOfWeek/MONDAY) (LocalTime/of 10 30) (LocalTime/of 11 30))
                       (Timeslot. (DayOfWeek/MONDAY) (LocalTime/of 13 30) (LocalTime/of 14 30))
                       (Timeslot. (DayOfWeek/MONDAY) (LocalTime/of 14 30) (LocalTime/of 15 30)))
        roomList (list
                   (Room. "Room A")
                   (Room. "Room B")
                   (Room. "Room C"))
        lessonList (list
                     (Lesson. 101 "Math" "B. May" "9th grade")
                     (Lesson. 102 "Physics" "M. Curie" "9th grade")
                     (Lesson. 103 "Geography" "M. Polo" "9th grade")
                     (Lesson. 104 "English" "I. Jones" "9th grade")
                     (Lesson. 105 "Spanish" "P. Cruz" "9th grade")
                     
                     (Lesson. 201 "Math" "B. May" "10th grade")
                     (Lesson. 202 "Chemistry" "M. Curie" "10th grade")
                     (Lesson. 203 "History" "I. Jones" "10th grade")
                     (Lesson. 204 "English" "P. Cruz" "10th grade")
                     (Lesson. 205 "French" "M. Curie" "10th grade"))]
    (TimeTable. timeslotList roomList lessonList)))

(defn solve [problem]
  (let [sm (SolverManager/create
             (SolverFactory/create
               (.withTerminationConfig
                 (doto (SolverConfig.)
                   (.setSolutionClass TimeTable)
                   (.setEntityClassList (Collections/singletonList Lesson))
                   (.setScoreDirectorFactoryConfig
                     (doto (ScoreDirectorFactoryConfig.)
                       (.setConstraintProviderClass TimeTableConstraintProvider))))
                 (doto (TerminationConfig.)
                   (.setSecondsSpentLimit 10)))))
        problemId (UUID/randomUUID)
        solution (.getFinalBestSolution
                   (.solve sm problemId problem))]
    
    (list (map #(vector (.getId %)
                        ;; (.getSubject %)
                        ;; (.getTeacher %)
                        ;; (.getStudentGroup %)
                        (.toString (.getTimeslot %))
                        (.toString (.getRoom %)))
               (.getLessonList solution))
          (.getScore solution))
    )
  )


(comment
  (solve (generateProblem))
  )
