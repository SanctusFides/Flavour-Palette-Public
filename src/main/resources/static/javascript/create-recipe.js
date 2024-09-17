// Setting a global variable to keep track of steps for page content and id generation
let stepCount = 1;
let ingrCount = 1;
let totalTime = 0;
var sizeArray = ["Size (Optional)","Teaspoons","Tablespoons","Cups","Ounces","Pints","Quarts","Gallons"];

// This section below is for everything related to updating and repainting the Total Time when prep and cook are updated
const prepBox = document.getElementById("prepTime");
const cookBox = document.getElementById("cookTime");

function calcTotal() {
  let newPrep = document.getElementById("prepTime");
  let newCook = document.getElementById("cookTime");
  // If-Else block checks if value of input box is empty, if so then update total time to the non-empty value or 0 if both empty
  if (newPrep.value === "" && newCook.value === "") {
    totalTime = 0;
  } else if (newPrep.value === "") {
    totalTime = newCook.value;
  } else if (newCook.value === "") {
    totalTime = newPrep.value;
  } else {
    totalTime = parseInt(newPrep.value) + parseInt(newCook.value);
  }
}
function paintTotal() {
  // first line is to remove the old element to be replaced with the updated time element
  document.getElementById("total-input").remove();
  let newTotal = document.createElement("input");
  newTotal.disabled = true;
  newTotal.setAttribute("class","form-time");
  newTotal.setAttribute("id","total-input")
  newTotal.value = totalTime;

  let totalDiv = document.getElementById("totalTime");
  totalDiv.append(newTotal);
}
function updateTotalTime() {
  calcTotal();
  paintTotal();
}

prepBox.addEventListener("input", updateTotalTime);
cookBox.addEventListener("input", updateTotalTime);
// End of Total Time section

function addStep() {
  stepCount++;
  const instructions = document.querySelector(".instructions");

  let newDiv = document.createElement("div");
  newDiv.setAttribute("class", "instruction border");
  newDiv.setAttribute("id", `step-${stepCount}`);
  newDiv.setAttribute("name", "instruction");

  let newLabel = document.createElement("label");
  newLabel.setAttribute("class", "form-label spacer");
  newLabel.setAttribute("name", "step-label");

  let newSpan = document.createElement("span");
  newSpan.setAttribute("class", "step-label-span");
  newSpan.textContent = `Enter instructions for Step ${stepCount}`;

  let newTextArea = document.createElement("textarea");
  newTextArea.setAttribute("id", `instructionList${stepCount-1}.stepText`);

  newTextArea.setAttribute("class", "form-desc spacer-bottom stepText");
  newTextArea.setAttribute("name", `instructionList[${stepCount-1}].stepText`);
  newTextArea.required = true;

  newLabel.appendChild(newSpan);
  newLabel.appendChild(newTextArea);
  newDiv.appendChild(newLabel);

  let deleteStepBtn = deleteInstrStep();
  newDiv.appendChild(deleteStepBtn);
  
  instructions.appendChild(newDiv);
  return;
}

function addIngr() {
  ingrCount++;
  document.getElementById("ingrCount").value = ingrCount;
  const ingredients = document.querySelector(".ingredients");

  let parentDiv = document.createElement("div");
  parentDiv.setAttribute("class", "form-ingr-label spacer");

  let newDiv = document.createElement("div");
  newDiv.setAttribute("class", "ingredient border-bottom");
  newDiv.setAttribute("id", `ingr-${ingrCount}`);

  let nameLabel = document.createElement("label");
  nameLabel.setAttribute("class", "form-ingr-label spacer");
  nameLabel.textContent = `Name`;

  let name = document.createElement("input");
  name.setAttribute("type", "text");
  name.setAttribute("name", `ingredientList[${ingrCount-1}].name`);
  name.setAttribute("class", "form-ingredient-name");
  name.setAttribute("placeholder", "Ingredient Name");
  name.required = true;

/* Child div is for using the flexbox settings for the 3 elements for quantity, sizing and prep info
   - they append to child and child appends to newDiv */
  let childDiv = document.createElement("div");
  childDiv.setAttribute("class", "form-ingredient-multi");

  let qtyDiv = document.createElement("div");
  let sizeDiv = document.createElement("div");
  let prepDiv = document.createElement("div");
  qtyDiv.setAttribute("class","form-ingredient-qty-div");
  sizeDiv.setAttribute("class","form-ingredient-size-div");
  prepDiv.setAttribute("class","form-ingredient-prep-div")

  let qtyLabel = document.createElement("label");
  qtyLabel.setAttribute("class","form-ingr-label");
  qtyLabel.textContent = `Quantity`

  let sizeLabel = document.createElement("label");
  sizeLabel.setAttribute("class","form-ingr-label");
  sizeLabel.textContent = `Size (Optional)`;

  let prepLabel = document.createElement("label");
  prepLabel.setAttribute("class","form-ingr-label");
  prepLabel.textContent = `Prep Info (Optional)`;

  let qty = document.createElement("input");
  qty.setAttribute("type", "text");
  qty.setAttribute("name", `ingredientList[${ingrCount-1}].quantity`);
  qty.setAttribute("class", "form-ingredient-qty");
  qty.setAttribute("placeholder", "Qty");
  qty.required = true;

  let size = document.createElement("select");
  size.setAttribute("name", `ingredientList[${ingrCount-1}].size`);
  size.setAttribute("class", "form-ingredient-size");
  // Loops through the size array @ top of file and creates options. 
  for (var i = 0; i < sizeArray.length; i++) {
    var option = document.createElement("option");
    if (sizeArray[i] == "Size (Optional)") {
      option.selected = true;
      option.value = "None";
      option.text = sizeArray[i];
      size.appendChild(option);
    } else {
      option.value = sizeArray[i];
      option.text = sizeArray[i];
      size.appendChild(option);
    }
  }

  let prep = document.createElement("input");
  prep.setAttribute("type", "text");
  prep.setAttribute("name", `ingredientList[${ingrCount-1}].prepType`);
  prep.setAttribute("class", "form-ingredient-prep");
  prep.setAttribute("placeholder", "(ex: minced, diced...)");

  nameLabel.appendChild(name);
  newDiv.appendChild(nameLabel);
  newDiv.appendChild(childDiv);

  qtyLabel.appendChild(qty);
  sizeLabel.appendChild(size);
  prepLabel.appendChild(prep);

  qtyDiv.appendChild(qtyLabel);
  sizeDiv.appendChild(sizeLabel);
  prepDiv.appendChild(prepLabel);

  childDiv.appendChild(qtyDiv);
  childDiv.appendChild(sizeDiv);
  childDiv.appendChild(prepDiv);

  let removeIngrBtn = deleteIngrBtn();
  newDiv.appendChild(removeIngrBtn);

  ingredients.appendChild(newDiv);
  return;
}

// Separated the create/delete button logic into own function in case I need to change how this is made in the future
function createStepBtn() {
  const addStepBtn = document.createElement("button");
  addStepBtn.setAttribute("type", "button");
  addStepBtn.setAttribute("class", "add-instr");
  addStepBtn.setAttribute("id", "addStepBtn");
  addStepBtn.textContent = "Add a Step";

  addStepBtn.addEventListener("click", (event) => {
    addStep();
  });

  return addStepBtn;
}

function createIngrBtn() {
  const addIngrBtn = document.createElement("button");
  addIngrBtn.setAttribute("class","add-ingr");
  addIngrBtn.setAttribute("id", "addIngrBtn");
  addIngrBtn.setAttribute("type", "button");
  addIngrBtn.textContent = "Add Ingredient";

  addIngrBtn.addEventListener("click", (event) => {
    addIngr();
  });

  return addIngrBtn;
}

function deleteIngrBtn() {
  const deleteIngrBtn = document.createElement("button");
  deleteIngrBtn.setAttribute("class", "rmv-ingr bottom-gap")
  deleteIngrBtn.setAttribute("type", "button");
  deleteIngrBtn.textContent = "Remove Ingredient";

  deleteIngrBtn.addEventListener("click", (event) => {
    ingrCount--;
    document.getElementById("ingrCount").value = ingrCount;
    deleteIngrBtn.parentElement.remove();
  });
  return deleteIngrBtn;
}


function deleteInstrStep() {
  const deleteStepBtn = document.createElement("button");
  deleteStepBtn.setAttribute("class", "rmv-instr")
  deleteStepBtn.setAttribute("type", "button");
  deleteStepBtn.setAttribute("id", "dlt-btn");
  deleteStepBtn.textContent = "Remove Step";

  deleteStepBtn.addEventListener("click", (event) => {
    stepCount--;
//  Delete this if it's not needed - it's in the Edit version
//        document.getElementById("stepCount").value = stepCount;
    deleteStepBtn.parentElement.remove();
    repaintSteps();
//    var instrLabelList = document.getElementsByName("step-label");
//    for (let i = 0; i < stepCount; i++) {
//      instrLabelList[i].innerHTML = `Enter instructions for Step ${i+1}`;
//    }
  });
  return deleteStepBtn;
}

function repaintSteps() {
  let instrList = document.getElementsByClassName("instruction");
  let instrInputs = document.getElementsByClassName("stepText");
  let instrLabels = document.getElementsByClassName("step-label-span");
  for (let i = 0; i < instrList.length; i++) {
    instrList[i].setAttribute("id", "step-"+(i+1));
    instrInputs[i].setAttribute("id","instructionList" +i+ ".stepText");
    instrInputs[i].setAttribute("name","instructionList[" +i+ "].stepText");
    instrLabels[i].textContent = "Enter instructions for Step " + (i+1);
  }
}
