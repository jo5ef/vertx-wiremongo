package com.noenv.wiremongo.command;

import io.vertx.codegen.annotations.DataObject;

public abstract class CommandBase implements Command {

  private final String method;

  public CommandBase(String method) {
    this.method = method;
  }

  @Override
  public String method() {
    return method;
  }

  @Override
  public String toString() {
    return "method: " + method;
  }
}
