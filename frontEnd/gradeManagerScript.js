"use strict";

    function getOptions(courses) {
        var options = ko.mapping.toJS(courses);
        var opt = [];
        options.forEach(function (elem, index) {
            opt.push(elem["courseName"]);
        });
        return opt;

    }


    function validateFields(fields){
        var flag = false;
        for(var i=0; i<fields.length; i++){
            flag = flag || fields[i]==="";
        }
        return flag;
    }

    function readInputFields(selector){
        var fields = [];
        selector.each(function () {
            fields.push($(this).val());
        });

        selector.each(function () {
            $(this).val("");
        });
        return fields;

    }

    function getInputStudent(){
        var selector = $("tfoot.addStudent input");
        var fields = readInputFields(selector);

        if (validateFields(fields)) {
            alert("input not valid");
            return;
        }

        return new Student(fields[0], fields[1], fields[2], fields[3], []);

    }

    function getInputGrade(){
        var courseName = $("tfoot.addGrade select").val();
        var selector = $("tfoot.addGrade input");
        var fields = readInputFields(selector);

        if (validateFields(fields)) {
            alert("input not valid");
            return;
        }

        return new Grade(fields[0], fields[1], courseName);
    }

    function getInputCourse(){
        var selector = $("tfoot.addCourse input");
        var fields = readInputFields(selector);

        if (validateFields(fields)) {
            alert("input not valid");
            return;
        }

        return new Course(fields[0], fields[1], fields[2]);

    }

    function Grade(gradeDate, gradeValue, courseName){
        this.gradeDate = gradeDate;
        this.gradeValue = gradeValue;
        this.courseName = courseName;
    }

    function Course(courseName, lecturer, surname){
        this.courseName = courseName;
        this.lecturer = lecturer;
        this.surname = surname;
    }

    function Student(indexNumber, firstName, lastName, birthday, grades){
        this.indexNumber = indexNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.grades = grades;
    }



    function mapGrade(grades){
        var mapped = [];
        grades.forEach(function(item){
            var grade = new Grade(item.gradeDate, item.gradeValue, item.course.courseName);
            mapped.push(ko.mapping.fromJS(grade))
        });
        return mapped;
    }

    function mapStudentsToObservables(items){
        var result = [];
        for(var i=0; i<items.length; i++){
            items[i].grades = mapGrade(items[i].grades);
            var data = ko.mapping.fromJS(items[i]);
            result.push(data);
        }
        return ko.mapping.fromJS(result);
    }

    function mapCoursesToObservables(items){
        var result = items.map(function(item, index, array){
            return ko.mapping.fromJS(item);
        });
        return ko.mapping.fromJS(result);
    }


    function refreshStudents(students, courseName){
        students().forEach(function(student, index, array){
            student.grades.remove(function(grade){
                return grade.courseName() === courseName;
            });
        })

    }



    var ListViewModel = function (students, courses) {
        var self = this;
        self.students = mapStudentsToObservables(students);

        self.selectedStudent = {
            name: ko.observable(""),
            surname: ko.observable(""),
            index: ko.observable(""),
            grades: ko.observableArray()
        };
        self.data = ko.computed(function(){
            return self.selectedStudent.name() + " " + self.selectedStudent.surname();
        });

        self.courses = mapCoursesToObservables(courses);
        self.options = ko.observableArray(getOptions(self.courses));

        self.searching = {
            searchName: ko.observable(""),
            searchSurname: ko.observable(""),
            searchBirthday: ko.observable(""),
            searchIndex: ko.observable("")
        };

        self.courseSearching = {
            searchCourse: ko.observable(""),
            searchLecturer: ko.observable(""),
            searchSurname: ko.observable("")
        };


        self.addStudent = function () {
            var newStudent = getInputStudent();
            if(newStudent !== undefined){
                self.students.push(ko.mapping.fromJS(newStudent));
                sendData("http://localhost:8000/gradeManager/students", "POST", newStudent);
            }

        };

        self.removeStudent = function (student) {
            self.students.remove(student);
            var num = student.indexNumber();
            deleteData("http://localhost:8000/gradeManager/students/" + num);
        };

        self.updateStudent = function (student) {
            var num = student.indexNumber();
            student.grades = [];
            sendData("http://localhost:8000/gradeManager/students/" + num, "PUT", ko.mapping.toJS(student));
        };



        self.showGrades = function (student) {
            window.location.href = "#grades";

            self.selectedStudent
                .index(student.indexNumber())
                .name(student.firstName())
                .surname(student.lastName())
                .grades(student.grades());

        };

        self.addGrade = function () {
            var newGrade = getInputGrade();
            if(newGrade !== undefined){
                var studentIndex = self.selectedStudent.index();
                self.selectedStudent.grades.push(ko.mapping.fromJS(newGrade));
                var courseName = newGrade.courseName;
                var url = "http://localhost:8000/gradeManager/students/" + studentIndex + "/grades/" + courseName;
                sendData(url, "POST", {gradeDate: newGrade.gradeDate, gradeValue: newGrade.gradeValue});
            }

        };

        self.removeGrade = function (grade) {
            var studentIndex = self.selectedStudent.index();
            var position = self.selectedStudent.grades.indexOf(grade);
            self.selectedStudent.grades.remove(grade);
            deleteData("http://localhost:8000/gradeManager/students/" + studentIndex + "/grades/" + position);

        };

        self.updateGrade = function (grade) {
            var studentIndex = self.selectedStudent.index();
            var position = self.selectedStudent.grades.indexOf(grade);
            var url = "http://localhost:8000/gradeManager/students/" + studentIndex + "/grades/" + position;
            sendData(url, "PUT", {gradeDate: grade.gradeDate(), gradeValue: grade.gradeValue()});
        };



        self.addCourse = function () {
            var newCourse = getInputCourse();
            if(newCourse !== undefined){
                self.courses.push(ko.mapping.fromJS(newCourse));
                self.options.push(newCourse.courseName);
                sendData("http://localhost:8000/gradeManager/courses", "POST", newCourse);
            }
        };

        self.removeCourse = function (course) {
            var courseName = course.courseName();
            refreshStudents(self.students, courseName);
            self.courses.remove(course);
            self.options.remove(course.courseName());
            deleteData("http://localhost:8000/gradeManager/courses/" + courseName);
        };

        self.updateCourse = function (course) {
            var courseName = course.courseName();
            sendData("http://localhost:8000/gradeManager/courses/" + courseName, "PUT", ko.mapping.toJS(course));
        }


    };


    function subscribeSearchInterface(model){

        Object.keys(model.searching).forEach(function(key){
            model.searching[key].subscribe(onStudentValueChanged)
        });

        Object.keys(model.courseSearching).forEach(function(key){
            model.courseSearching[key].subscribe(onCourseValueChanged)
        });
    }

    function onStudentValueChanged(){
        var searchData = ko.mapping.toJS(myModel.searching);
        var data = $.param(searchData);
        console.log(data);
        sendSearchData(data, "http://localhost:8000/gradeManager/students/searching", callStudents);
    }

    function onCourseValueChanged(){
        var searchData = ko.mapping.toJS(myModel.courseSearching);
        sendSearchData($.param(searchData), "http://localhost:8000/gradeManager/courses/searching", callCourses)
    }

    function callStudents(students){
        var obsStudents = mapStudentsToObservables(students);
        myModel.students(obsStudents());
        //ko.mapping.fromJS(students, myModel.students);
    }
    function callCourses(courses){
        var obsCourses = mapCoursesToObservables(courses);
        myModel.courses(obsCourses());
        //ko.mapping.fromJS(courses, myModel.courses);
    }


    function deleteData(urls) {
        $.ajax({
            url: urls,
            type: "DELETE",
            dataType: "json",
            contentType: "application/json"
        });
    }

    function sendData(urls, method, data1) {
        $.ajax({
            url: urls,
            data: JSON.stringify(data1),
            type: method,
            dataType: "json",
            contentType: "application/json"

        });
    }

    function sendSearchData(data, urls, callback) {
        $.ajax({
            url: urls,
            data: data,
            type: "GET",
            dataType: "json",
            contentType: "application/json",
            success: function (result) {
                callback(result);
            }
        });
    }


var myModel;

$(document).ready(function (){

    $.when(
        $.ajax({
            url: "http://localhost:8000/gradeManager/students",
            type: "GET",
            dataType: "json",
            success: function (result) {
                console.log("students readed")
            }
        }),
        $.ajax({
            url: "http://localhost:8000/gradeManager/courses",
            type: "GET",
            dataType: "json",
            success: function (result) {
                console.log("courses readed")
            }
        })

    ).then(function(students, courses){
        myModel = new ListViewModel(students[0], courses[0]);
        ko.applyBindings(myModel);
        subscribeSearchInterface(myModel);

    });


});